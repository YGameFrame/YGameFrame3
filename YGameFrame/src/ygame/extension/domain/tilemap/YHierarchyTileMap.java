package ygame.extension.domain.tilemap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ygame.domain.YADomainLogic;
import ygame.domain.YDomain;
import ygame.domain.YDomainView;
import ygame.exception.YException;
import ygame.extension.domain.tilemap.YTiledBean.YLayer;
import ygame.extension.domain.tilemap.YTiledBean.YTileSet;
import ygame.extension.program.YTextureProgram;
import ygame.extension.program.YTextureProgram.YAdapter;
import ygame.framework.core.YABaseDomain;
import ygame.framework.core.YRequest;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;
import ygame.skeleton.YSkeleton;
import ygame.texture.YTexture;
import ygame.transformable.YMover;
import ygame.utils.YBitmapUtils;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.RectF;

//支持1 tileset -> 多 layer，但不支持1 layer -> 多tileset
//但目前支持1索引图对应1layer，一索引图对应多layer有bug，正在解决中
final class YHierarchyTileMap extends YATileMapDomain
{
	private static final int DEFAULT_PIXELS_PER_UNIT = 32;
	private static final float MAP_DEEPTH = 0.01f;

	private static final String TAG = YHierarchyTileMap.class
			.getSimpleName();

	private Map<String, RectF[]> tileSetsCoords = new HashMap<String, RectF[]>();

	private Resources resources;

	protected YHierarchyTileMap(String KEY, String jsonFileNameInAsset,
			Context context)
	{
		super(KEY, jsonFileNameInAsset, context);
		this.resources = context.getResources();
	}

	@Override
	protected void onAttach(YSystem system)
	{
		super.onAttach(system);

		// 1.生成砖块集合（索引图集合）纹理信息
		YTileSet[] tilesets = tiledBean.getTilesets();
		for (YTileSet tileSet : tilesets)
			tileSetsCoords.put(tileSet.getName(),
					buildTileSetCoords(tileSet));

		// 2.确认“单位1”
		float fPixelsPerUnit = confirmPixelsPerUnit();

		// 3.地图中一个图层视为一个实体(Domain)，
		// 以下代码通过循环生成每一层实体并加入集合，构建完整的地图
		ArrayList<YABaseDomain> domains = new ArrayList<YABaseDomain>();
		YLayer[] layers = tiledBean.getLayers();
		for (int i = 0; i < layers.length; i++)
		{
			final String layerDomainKey = KEY + "_layer_" + i;
			YDomain domain = buildLayerDomain(fPixelsPerUnit,
					layers[i], layerDomainKey);
			domains.add(domain);
		}
		addComponentDomains(domains);
	}

	private YDomain buildLayerDomain(float fPixelsPerUnit, YLayer layer,
			String key)
	{
		YTileSet tileSet = chooseTileSetsByLayer(layer);
		//@formatter:off
		YTileData[] datas = calculateTileMetaDatas(fPixelsPerUnit,layer, tileSet);
		//经过上步，这里我们已经得到足以计算每个砖块的完整位置和纹理坐标的关键信息
		//接下来要做的就是利用关键信息计算出完整的坐标，并将之连为一片
		float[] fPos = calculateAndConcatTilesPos(datas, 
				tileSet.getTilewidth()/ fPixelsPerUnit, 
				tileSet.getTileheight()/ fPixelsPerUnit);
		float[] fTexCoord = calculateAndConcatTilesTexCoords(datas);
		//这里我们已经得到该层图层的所有顶点位置坐标和纹理坐标，于是可以构建骨架
		YTileSkeleton skeleton = new YTileSkeleton(fPos, fTexCoord);
		//当骨架确定之后，万事大吉！随后新建实体即可
		YDomain domain = new YDomain(
				key,
				new YTileMapLogic(skeleton,new YTexture(getBitmapByName(tileSet.getName()))),
				new YDomainView(YTextureProgram.getInstance(resources)));
		//@formatter:on
		return domain;
	}

	/**
	 * 生成每个<b>有效砖块</b>的<b>元数据</b> <li>元数据指——计算砖块位置坐标和纹理坐标时用到的关键信息 <li>
	 * 有效砖块指——该砖块拥有一块来自于索引图的图像（我们在利用Tiled设计
	 * 地图的时候时常会有“空砖块”——我们不会在那个小块上盖上索引图印章的砖块）
	 */
	private YTileData[] calculateTileMetaDatas(final float fPixelsPerUnit,
			YLayer layer, YTileSet tileSet)
	{
		// 1.根据layer中的数据生成砖块元数据，并填写砖块对应的纹理坐标信息
		Collection<YTileData> tileDatas = generateTileMedaDatasAndFillTexCoordInfos(
				layer, tileSet);
		// 2.在上一步生成的砖块数据里填写位置坐标信息
		YTileData[] tileData1dArray = fillTileMetaDataPositionInfos(
				fPixelsPerUnit, layer, tileDatas);
		// 3.过滤掉layer.data中为0的砖块（占位砖块），只保留有效砖块
		return filterPlaceHolderTileData(tileData1dArray);
	}

	private YTileData[] filterPlaceHolderTileData(
			YTileData[] tileData1dArray)
	{
		Collection<YTileData> validDatas = new ArrayList<YTileData>();
		for (int i = 0; i < tileData1dArray.length; i++)
			if (YTileData.placeholderTileData != tileData1dArray[i])
				validDatas.add(tileData1dArray[i]);
		YTileData[] datas = validDatas.toArray(new YTileData[0]);
		return datas;
	}

	/**
	 * 在给定的{@code tileDatas}填写每个砖块的<b>位置坐标信息</b> </p>
	 * 注意本处只是<b>位置坐标信息</b>，而非真正的位置坐标（实际上，本方法只是计算出了 每个砖块的左上角的顶点位置坐标），之后会根据该信息
	 * 计算实际的位置坐标
	 */
	private YTileData[] fillTileMetaDataPositionInfos(
			final float fPixelsPerUnit, YLayer layer,
			Collection<YTileData> tileDatas)
	{
		// 1.将1维的tileDatas变为2维，方便计算其位置
		final int rows = layer.getHeight();
		final int cols = layer.getWidth();
		YTileData[][] tileData2dArray = monoToBidi(
				tileDatas.toArray(new YTileData[0]), rows, cols);
		//@formatter:off
		// 2.根据2维的tileDatas计算其左上角顶点的坐标信息
		final float fZ = calculateZByLayerIndex(getLayerIndex(layer));
		YTileSet tileSet = chooseTileSetsByLayer(layer);
		final float fWidth = tileSet.getTilewidth() / fPixelsPerUnit;
		final float fHeight = tileSet.getTileheight() / fPixelsPerUnit;
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				tileData2dArray[r][c].init(c * fWidth, -r* fHeight, fZ);
		//@formatter:on
		// 3.恢复2维数据为1维数据，返回结果
		YTileData[] tileData1dArray = bidiToMono(tileData2dArray);
		return tileData1dArray;
	}

	//@formatter:off
	/**
	 * 该方法完成两个任务： 
	 * <li>
	 * 为图层{@code layer}中的每个砖块生成一个<b>元数据</b>{@link YTileData}，
	 * 以便之后计算时描述每个砖块的位置坐标和纹理坐标
	 * <li>
	 * 在生成的元数据中填入砖块对应的<b>索引图纹理坐标信息</b>，注意本处只是填入
	 * <b>索引图纹理坐标信息</b>，而非真正的纹理坐标，之后会利用该<b>索引图纹理坐标信息</b>
	 * 算出实际的纹理坐标
	 */
	//@formatter:on
	private Collection<YTileData> generateTileMedaDatasAndFillTexCoordInfos(
			YLayer layer, YTileSet tileSet)
	{
		RectF[] textureCoords = tileSetsCoords.get(tileSet.getName());
		int[] layerDatas = layer.getData();
		final int firstgid = tileSet.getFirstgid();
		Collection<YTileData> tileDatas = new ArrayList<YHierarchyTileMap.YTileData>();
		for (int data : layerDatas)
		{
			if (0 != data)
			{
				YTileData datas = new YTileData(
						textureCoords[data - firstgid]);
				tileDatas.add(datas);
			} else
				// 对于“空砖块”，我们简单地填上一个“占位符”，以便之后计算
				// 有效砖块的位置坐标，最终处理时，这些“空砖块”会被过滤掉
				tileDatas.add(YTileData.placeholderTileData);
		}

		return tileDatas;
	}

	//@formatter:off
	/**
	 * 选择指定图层对应的索引图</p> 
	 * 目前只支持<b>一个图层的砖块来自于一个索引图</b>，不支持一个图层的砖块来自于
	 * 多个索引图；</br>
	 * 不过目前两个图层的砖块同时来自于一个索引图会产生黑块，
	 * 正在解决中， 在解决该问题前，请让索引图和图层一一对应
	 * @param layer 指定图层
	 * @return 图层对应的索引图
	 */
	//@formatter:on
	private YTileSet chooseTileSetsByLayer(YLayer layer)
	{
		int[] datas = layer.getData();
		int validData = 0;
		for (int i = 0; i < datas.length; i++)
			if (0 != (validData = datas[i]))
				break;

		if (0 == validData)
			throw new YException("无效的图层", TAG, "图层"
					+ layer.getName() + "全为无效数据-0！");

		return queryTileSetByValidData(validData);
	}

	private YTileSet queryTileSetByValidData(int validData)
	{
		YTileSet[] tilesets = tiledBean.getTilesets();
		SortedMap<Integer, YTileSet> map = new TreeMap<Integer, YTiledBean.YTileSet>();
		for (YTileSet tileSet : tilesets)
			map.put(tileSet.getFirstgid(), tileSet);

		Set<Integer> firstgids = map.keySet();
		List<Integer> firstgidList = new ArrayList<Integer>(firstgids);
		int pre = -1;
		for (Integer firstgid : firstgidList)
		{
			if (validData < firstgid)
				return map.get(firstgidList.get(pre));
			else
				pre++;
		}
		return map.get(firstgidList.get(pre));
	}

	// 生成指定砖块集合的纹理坐标
	private RectF[] buildTileSetCoords(YTileSet tileSet)
	{
		Rect[] rects = YBitmapUtils.splitBitmapOnlyForMetaData(
				getBitmapByName(tileSet.getName()),
				tileSet.getImagewidth()
						/ tileSet.getTilewidth(),
				tileSet.getImageheight()
						/ tileSet.getTileheight());
		ArrayList<RectF> textureCoordList = new ArrayList<RectF>();
		for (Rect rect : rects)
			textureCoordList.add(toTextureCoord(rect,
					tileSet.getImagewidth(),
					tileSet.getImageheight()));
		return textureCoordList.toArray(new RectF[0]);
	}

	/** 计算每个砖块的位置坐标 ， 并将所有的顶点坐标连成一片 */
	private float[] calculateAndConcatTilesPos(YTileData[] validDatas,
			float fTileWidth, float fTileHeight)
	{
		float[] fPos = null;
		for (int i = 0; i < validDatas.length; i++)
			fPos = concat(fPos, validDatas[i].generateSixVertexs(
					fTileWidth, fTileHeight,
					tiledBean.getWidth() * fTileWidth
							/ 2.0f,
					tiledBean.getHeight() * fTileHeight
							/ 2.0f));
		return fPos;
	}

	/** 计算每个砖块的纹理坐标 ， 并将所有的纹理坐标连成一片 */
	private float[] calculateAndConcatTilesTexCoords(YTileData[] validDatas)
	{
		float[] fTexCoords = null;
		for (int i = 0; i < validDatas.length; i++)
			fTexCoords = concat(fTexCoords,
					validDatas[i].generateSixTexCoords());
		return fTexCoords;
	}

	private YTileData[][] monoToBidi(final YTileData[] array,
			final int rows, final int cols)
	{
		if (array.length != (rows * cols))
			throw new IllegalArgumentException(
					"Invalid array length");

		YTileData[][] bidi = new YTileData[rows][cols];
		for (int i = 0; i < rows; i++)
			System.arraycopy(array, (i * cols), bidi[i], 0, cols);

		return bidi;
	}

	private YTileData[] bidiToMono(final YTileData[][] array)
	{
		int rows = array.length, cols = array[0].length;
		YTileData[] mono = new YTileData[(rows * cols)];
		for (int i = 0; i < rows; i++)
			System.arraycopy(array[i], 0, mono, (i * cols), cols);
		return mono;
	}

	private RectF toTextureCoord(Rect rect, float fGlobalWidth,
			float fGlobalHeight)
	{
		// 规格化
		RectF rectF = new RectF(rect);
		rectF.top = rectF.top / fGlobalHeight;
		rectF.bottom = rectF.bottom / fGlobalHeight;
		rectF.left = rectF.left / fGlobalWidth;
		rectF.right = rectF.right / fGlobalWidth;
		return rectF;
	}

	private int getLayerIndex(YLayer layer)
	{
		YLayer[] layers = tiledBean.getLayers();
		for (int i = 0; i < layers.length; i++)
			if (layers[i] == layer)
				return i;
		throw new YException("查找不到layer_" + layer.getName() + "的索引",
				TAG, "");
	}

	//@formatter:off
	/**
	 * <b>确定Tiled映射到引擎中的单位长度。</b> 
	 * <p style="text-indent: 2em"> 
	 * Tiled中是以像素为单位的， 例如，64X64的小块等，
	 * 但引擎中是以一个抽象的“单位1” 为单位的， 例如，1X1的小块等。
	 * 我们实际上不用深究这个单位1到底是多少以及怎样去衡量，
	 * 因为1X1的小块随时会随着我们的摄像头拉远拉近而在屏幕上变小变大。
	 * 我们要做的是确定一个映射标准——在Tiled中多少个像素相抵与引擎的一个 “单位1”？
	 * 现在的约定是，默认
	 * {@link #DEFAULT_PIXELS_PER_UNIT}像素相抵于一个“单位1”，当然，
	 * 我们可以在Tiled编辑地图时创建一个自定义属性——"pixels_per_unit"，通过设定其
	 * 值从而改变它。也就是说如果没有这个操作的话，默认"pixels_per_unit"为
	 * {@link #DEFAULT_PIXELS_PER_UNIT}。
	 * </p>
	 * <p style="text-indent: 2em"> 
	 * 这样设计带来的一个好处是，box2d中的单位长度是“米”，如果我们使用了
	 * box2d，我们就可以赋予引擎里这个抽象的“单位1”以实际意义——“1米”，
	 * 然后我们可以根据现实生活中的常识去确定物体、人物等的高度、宽度、质量、质心
	 * 等等；比如，一个石块，应该有2米高，也就是说它的skeleton的高度为2，于是
	 * 我们可以根据常识去估计一个2米高的石块质量为多少从而在box2d中设置其密度，
	 * 再假设该石块对应的纹理图为128像素，那么顺利成章的有一个“单位1”=64像素，
	 * 于是我们可以在设计地图时，设置"pixels_per_unit"为64，这样的话，我们就能
	 * 保证我们设置的这个石块的表现出的物理行为符合常理——不会太轻像个气球，
	 * 也不会重得像颗核武器；当我们将它显示在屏幕上的时候，如果发现它太大或者太小， 
	 * 我们只需调整摄像头的距离即可。
	 *</p>
	 */
	//@formatter:on
	private float confirmPixelsPerUnit()
	{
		float fPixelsPerUnit = tiledBean.getProperties()
				.getPixels_per_unit();
		fPixelsPerUnit = fPixelsPerUnit <= 0 ? DEFAULT_PIXELS_PER_UNIT
				: fPixelsPerUnit;
		return fPixelsPerUnit;
	}

	/**
	 * 以下算法保证了： <li>
	 * 层序号越小，z坐标越靠后（这与YTiled的图层顺序对应是一致的，这样做保证了前层图层会盖住后层图层，而不是相反的结果），
	 * layers[0]是最后一层，它的z坐标为{@link #MAP_DEEPTH}。 <li>所有图层加起来的“厚度”小于
	 * {@link #MAP_DEEPTH}，使得视觉上看起来厚度近似为零。
	 * */
	private float calculateZByLayerIndex(int layerIndex)
	{
		return -(MAP_DEEPTH - MAP_DEEPTH / tiledBean.getLayers().length
				* layerIndex);
	}

	private static float[] concat(float[] first, float[] second)
	{
		if (null == first || 0 == first.length)
			return second;
		float[] result = Arrays.copyOf(first, first.length
				+ second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	private static class YTileData
	{
		private float fTop;
		private float fLeft;
		private RectF textureCoord;
		private float fZ;

		public YTileData(RectF rectF)
		{
			this.textureCoord = rectF;
		}

		private void init(float fLeft, float fTop, float fZ)
		{
			this.fLeft = fLeft;
			this.fTop = fTop;
			this.fZ = fZ;
		}

		/**
		 * 根据左上角顶点计算所有顶点的位置坐标，当然这里顺便做了Tiled原点与引擎原点坐标
		 * 的转换（Tiled中全局地图左上角为原点，朝下为y正方向，朝右为x正方向；引擎中全局地图中心为
		 * 原点，朝上为y正方向，朝右为x正方向）
		 */
		private float[] generateSixVertexs(float fTileWidth,
				float fTileHeight, float fXOffset,
				float fYOffset)
		{
			float[] leftTopPos =
			{ fLeft - fXOffset, fTop + fYOffset, fZ };
			float[] rightTopPos =
			{ fLeft + fTileWidth - fXOffset, fTop + fYOffset, fZ };
			float[] rightBottom =
			{ fLeft + fTileWidth - fXOffset,
					fTop + fTileHeight + fYOffset, fZ };
			float[] leftBottom =
			{ fLeft - fXOffset, fTop + fTileHeight + fYOffset, fZ };

			float[] tmp = concat(leftTopPos, rightBottom);
			tmp = concat(tmp, leftBottom);
			tmp = concat(tmp, leftTopPos);
			tmp = concat(tmp, rightTopPos);
			return concat(tmp, rightBottom);
		}

		/**
		 * 根据纹理坐标信息计算砖块所有顶点的纹理坐标</p>这里需要注意的OpenGL纹理坐标系统与android图像坐标系统关于
		 * x轴呈镜像对称，所以计算完成之后我们需要反转一下
		 */
		private float[] generateSixTexCoords()
		{
			// float[] leftTopPos =
			// { textureCoord.left, textureCoord.top };
			// float[] rightTopPos =
			// { textureCoord.right, textureCoord.top };
			// float[] rightBottom =
			// { textureCoord.right, textureCoord.bottom };
			// float[] leftBottom =
			// { textureCoord.left, textureCoord.bottom };

			// OpenGL坐标与bitmap的坐标关于x轴镜像对称，故需上下颠倒一下
			float[] leftBottom =
			{ textureCoord.left, textureCoord.top };
			float[] rightBottom =
			{ textureCoord.right, textureCoord.top };
			float[] rightTopPos =
			{ textureCoord.right, textureCoord.bottom };
			float[] leftTopPos =
			{ textureCoord.left, textureCoord.bottom };

			float[] tmp = concat(leftTopPos, rightBottom);
			tmp = concat(tmp, leftBottom);
			tmp = concat(tmp, leftTopPos);
			tmp = concat(tmp, rightTopPos);
			return concat(tmp, rightBottom);
		}

		private static final YTileData placeholderTileData = new YTileData(
				null);
	}

	private static class YTileMapLogic extends YADomainLogic
	{

		private YMover mover = new YMover();
		private YSkeleton skeleton;
		private YTexture texture;

		public YTileMapLogic(YSkeleton skeleton, YTexture texture)
		{
			this.skeleton = skeleton;
			this.texture = texture;
		}

		@Override
		protected void onCycle(double dbElapseTime_s,
				YDomain domainContext, YWriteBundle bundle,
				YSystem system, YScene sceneCurrent,
				YMatrix matrix4pv, YMatrix matrix4Projection,
				YMatrix matrix4View)
		{
			YTextureProgram.YAdapter adapter = (YAdapter) domainContext
					.getParametersAdapter();
			adapter.paramMatrixPV(matrix4pv).paramMover(mover)
					.paramSkeleton(skeleton)
					.paramTexture(texture);
		}

		@Override
		protected boolean onDealRequest(YRequest request,
				YSystem system, YScene sceneCurrent,
				YBaseDomain domainContext)
		{
			return false;
		}
	}

	private static class YTileSkeleton extends YSkeleton
	{
		private YTileSkeleton(float[] pos, float[] texCoords)
		{
			setPositions(pos);
			setTexCoords(texCoords);
		}
	}
}
