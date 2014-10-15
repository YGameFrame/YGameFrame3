package ygame.extension.tiled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ygame.exception.YException;
import ygame.extension.domain.tilemap.YTiledBean;
import ygame.extension.domain.tilemap.YTiledBean.YLayer;
import ygame.extension.domain.tilemap.YTiledBean.YTileSet;
import ygame.framework.core.YABaseDomain;
import ygame.framework.core.YScene;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public final class YTiled
{
	private static final int DEFAULT_PIXELS_PER_UNIT = 32;

	private static final String TAG = YTiled.class.getSimpleName();

	private YScene scene;
	private Resources resources;

	private YTiledBean tiledBean;
	private Map<String, Bitmap> indexPics;

	final private float fPixelsPerUnit;

	YTiled(YScene scene, String jsonFileNameInAssert, Context context)
	{
		this.scene = scene;
		this.resources = context.getResources();

		tiledBean = YTiledBean.parseFromTiledJson(jsonFileNameInAssert,
				resources);
		indexPics = getIndexPics(context, tiledBean.getTilesets());
		fPixelsPerUnit = confirmPixelsPerUnit(tiledBean);
	}

	public void addToScene(YABaseDomain... domains)
	{
		scene.addDomains(domains);
		bInvokeAddToScence = true;
	}

	private boolean bInvokeAddToScence;

	void parse(Collection<YITiledParsePlugin> parsePlugins)
	{
		for (YITiledParsePlugin plugin : parsePlugins)
		{
			bInvokeAddToScence = false;
			// 当plugin解析实体完成后，框架要求客户调用addToScene将之加入场景
			plugin.parse(this, fPixelsPerUnit, resources);
			if (!bInvokeAddToScence)// 检查客户是否调用addToScene，未调用则报异常提醒客户
				throw new YException("您是否忘记调用addToScene(...)？",
						TAG,
						"实体解析完成后需要调用addToScene(...)将实体加入");
		}
	}

	private Map<String, Bitmap> getIndexPics(Context context,
			YTileSet[] tilesets)
	{
		Map<String, Bitmap> bmpMap = new HashMap<String, Bitmap>();
		for (YTileSet tileSet : tilesets)
		{
			String drawableName = tileSet.getImage();
			int i = drawableName.lastIndexOf('/');
			drawableName = drawableName.substring(i + 1);
			drawableName = drawableName.split("\\.")[0];
			int resId = resources.getIdentifier(drawableName,
					"drawable", context.getPackageName());
			Options options = new Options();
			options.inScaled = false;
			if (!bmpMap.containsKey(tileSet.getName()))
				bmpMap.put(tileSet.getName(), BitmapFactory
						.decodeResource(resources,
								resId, options));
		}
		return bmpMap;
	}

	/**
	 * 获取指定名称的{@code YLayer}
	 * 
	 * @param name
	 *                图层名字
	 * @return 指定图层
	 */
	public YLayer getLayerByName(String name)
	{
		YLayer[] layers = tiledBean.getLayers();
		for (YLayer layer : layers)
			if (name.equals(layer.getName()))
				return layer;
		throw new YException("没有找到YLayer:" + name, TAG, "");
	}

	/**
	 * 获取给定{@code YLayer}对应的索引位图对象，
	 * 引擎只支持<b>某个图层的索引图仅且仅来自于一个图层</b>，因此您可以通过该方法获取对应指定{@code layer}
	 * 对应的唯一索引位图对象。
	 * 
	 * @see {@link #getIndexPicturesByTileSet(YTileSet)}
	 * 
	 * @param layer
	 *                指定图层
	 * @return 索引图
	 */
	public Bitmap getIndexPictureByLayer(YLayer layer)
	{
		return indexPics.get(getTileSetsByLayer(layer).getName());
	}

	/**
	 * 获取给定{@code YTileSet}对应的索引位图对象，砖块集{@code YTileSet}仅包含诸如索引图的行列数等信息，
	 * 并未包含所对应的{@code Bitmap}，想要获取对应的位图对象，可以该方法获取。
	 * 
	 * @param tileSet
	 *                指定砖块集
	 * @return 索引图
	 */
	public Bitmap getIndexPicturesByTileSet(YTileSet tileSet)
	{
		return indexPics.get(tileSet.getName());
	}

	//@formatter:off
	/**
	 * 选择指定图层对应的索引图</p> 
	 * 目前只支持<b>一个图层的砖块来自于一个索引图</b>，不支持一个图层的砖块来自于
	 * 多个索引图；</br>
	 * 不过目前两个图层的砖块同时来自于一个索引图会产生黑块，
	 * 正在解决中， 在解决该问题前，请让索引图和图层一一对应
	 * @see {@linkplain #getIndexPictureByLayer(YLayer)}
	 * 
	 * @param layer 指定图层
	 * @return 图层对应的索引图
	 */
	//@formatter:on
	public YTileSet getTileSetsByLayer(YLayer layer)
	{
		if (YLayer.TYPE_TILE_LAYER.equals(layer.getType()))
		{
			int validData = getFirstValidDataOfLayer(layer);
			return queryTileSetByValidData(validData);
		}
		throw new YException("该图层属于：" + layer.getType()
				+ "，没有对应的砖块集（索引图）", TAG, "");
	}

	private int getFirstValidDataOfLayer(YLayer layer)
	{
		int[] datas = layer.getData();
		int validData = 0;
		for (int i = 0; i < datas.length; i++)
			if (0 != (validData = datas[i]))
				break;

		if (0 == validData)
			throw new YException("无效的图层", TAG, "图层"
					+ layer.getName() + "全为无效数据-0！");
		return validData;
	}

	private YTileSet queryTileSetByValidData(int validData)
	{
		YTileSet[] tilesets = tiledBean.getTilesets();
		SortedMap<Integer, YTileSet> map = new TreeMap<Integer, YTileSet>();
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
	private float confirmPixelsPerUnit(YTiledBean tiledBean)
	{
		float fPixelsPerUnit = tiledBean.getProperties()
				.getPixels_per_unit();
		fPixelsPerUnit = fPixelsPerUnit <= 0 ? DEFAULT_PIXELS_PER_UNIT
				: fPixelsPerUnit;
		return fPixelsPerUnit;
	}

	/**
	 * 获取总行数
	 * 
	 * @return 总行数
	 */
	public int getGlobalHeight()
	{
		return tiledBean.getHeight();
	}

	/**
	 * 获取总列数
	 * 
	 * @return 总列数
	 */
	public int getGlobalWidth()
	{
		return tiledBean.getWidth();
	}

	/**
	 * 获取一个砖块元的高度，像素为单位
	 * 
	 * @return 一个砖块元的高度，像素为单位
	 */
	public int getTileHeightInPixels()
	{
		return tiledBean.getTileheight();
	}

	/**
	 * 获取一个砖块元的宽度，像素为单位
	 * 
	 * @return 一个砖块元的宽度，像素为单位
	 */
	public int getTileWidthInPixels()
	{
		return tiledBean.getTilewidth();
	}
}
