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
	private static final int DEFAULT_UNIT_LEN = 2;

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

		// 1.生成瓷砖集合（索引图集合）纹理信息
		YTileSet[] tilesets = tiledBean.getTilesets();
		for (YTileSet tileSet : tilesets)
			tileSetsCoords.put(tileSet.getName(),
					buildTileSetCoords(tileSet));
		// 2.结合索引图中的纹理信息以及layer中的data信息生成每一层骨架（包含位置和纹理坐标），并新建实体添入集群实体中
		ArrayList<YABaseDomain> domains = new ArrayList<YABaseDomain>();
		float fUnitLength = tiledBean.getProperties().getUnitlength();
		fUnitLength = 0 == fUnitLength ? DEFAULT_UNIT_LEN : fUnitLength;
		YLayer[] layers = tiledBean.getLayers();
		for (int i = 0; i < layers.length; i++)
		// for (int i = 1; i < layers.length; i++)
		{
			// 根据layer选择该layer对应的瓷砖集合（索引图）
			YTileSet tileSet = chooseTileSetsByLayer(layers[i]);
			YTileData[] datas = calculateTileMetaDatas(fUnitLength,
					layers[i], i, tileSet);
			float[] fPos = concatTilesPos(datas, fUnitLength,
					fUnitLength);
			float[] fTexCoord = concatTilesTexCoords(datas);
			YTileSkeleton skeleton = new YTileSkeleton(fPos,
					fTexCoord);
			YDomain domain = new YDomain(
					KEY + "_layer_" + i,
					new YTileMapLogic(
							skeleton,
							new YTexture(
									getBitmapByName(tileSet
											.getName()))),
					new YDomainView(YTextureProgram
							.getInstance(resources)));
			domains.add(domain);
		}
		addComponentDomains(domains, system);
	}

	private YTileData[] calculateTileMetaDatas(final float fSideLength,
			YLayer layer, int layerIndex, YTileSet tileSet)
	{
		// 1.根据layer中的数据生成瓷砖数据，生成数据的同时根据其对应的索引图填充元纹理信息
		Collection<YTileData> tileDatas = generateTileMedaDatasAndFillTexCoordInfos(
				layer, tileSet);
		// 2.为瓷砖数据填写元位置信息
		YTileData[] tileData1dArray = fillTileMetaDataPositionInfos(
				fSideLength, layer, tileDatas, layerIndex);
		// 3.过滤掉layer.data中为0的瓷砖（占位瓷砖），只保留有效的瓷砖
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

	private YTileData[] fillTileMetaDataPositionInfos(
			final float fSideLength, YLayer layer,
			Collection<YTileData> tileDatas, int layerIndex)
	{
		// 1.将1维的tileDatas变为2维，方便计算其位置
		final int rows = layer.getHeight();
		final int cols = layer.getWidth();
		YTileData[][] tileData2dArray = monoToBidi(
				tileDatas.toArray(new YTileData[0]), rows, cols);
		// 2.根据2维的tileDatas计算其左上角顶点的坐标信息
		final float fDeepth = 0.01f;
		final float fZ = -(fDeepth - fDeepth
				/ tiledBean.getLayers().length * layerIndex);
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				tileData2dArray[r][c].init(c * fSideLength, -r
						* fSideLength, fZ);
		// 3.恢复2维数据为1维数据，返回结果
		YTileData[] tileData1dArray = bidiToMono(tileData2dArray);
		return tileData1dArray;
	}

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
				tileDatas.add(YTileData.placeholderTileData);
		}

		return tileDatas;
	}

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

	// 生成指定瓷砖集合的纹理坐标
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

	private float[] concatTilesPos(YTileData[] validDatas,
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

	private float[] concatTilesTexCoords(YTileData[] validDatas)
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

			// OpenGL坐标与bitmap的坐标关于y轴镜像对称
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

	private static float[] concat(float[] first, float[] second)
	{
		if (null == first || 0 == first.length)
			return second;
		float[] result = Arrays.copyOf(first, first.length
				+ second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
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
