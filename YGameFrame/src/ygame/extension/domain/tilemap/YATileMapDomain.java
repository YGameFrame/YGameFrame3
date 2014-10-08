package ygame.extension.domain.tilemap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.dynamics.World;

import ygame.extension.domain.tilemap.YTiledBean.YTileSet;
import ygame.extension.domain.tilemap.parse_plugin.YITileMapParsePlugin;
import ygame.framework.core.YABaseDomain;
import ygame.framework.core.YClusterDomain;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class YATileMapDomain extends YClusterDomain
{
	protected final YTiledBean tiledBean;
	private Map<String, Bitmap> bmpMap = new HashMap<String, Bitmap>();

	protected YATileMapDomain(String KEY, String jsonFileNameInAsset,
			Context context)
	{
		super(KEY);
		Resources resources = context.getResources();
		this.tiledBean = YTiledBean.parseFromTiledJson(
				jsonFileNameInAsset, resources);

		YTileSet[] tilesets = tiledBean.getTilesets();
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
			bmpMap.put(tileSet.getName(), BitmapFactory
					.decodeResource(resources, resId,
							options));
		}
	}

	protected final Bitmap getBitmapByName(String bitmapName)
	{
		return bmpMap.get(bitmapName);
	}

	public final static class YBuilder
	{
		private List<YITileMapParsePlugin> plugins = new ArrayList<YITileMapParsePlugin>();
		private String key;
		private String jsonFileName;
		private Context context;

		public YBuilder(String KEY, String jsonFileNameInAsset,
				Context context)
		{
			this.key = KEY;
			this.jsonFileName = jsonFileNameInAsset;
			this.context = context;
		}

		public YBuilder append(YITileMapParsePlugin plugin)
		{
			plugins.add(plugin);
			return this;
		}

		public YATileMapDomain build()
		{
			YATileMapDomain domainMap = new YATileMapDomain(key,
					jsonFileName, context);
			for (YITileMapParsePlugin plugin : plugins)
			{
				Collection<YABaseDomain> domains = plugin
						.parse(domainMap.tiledBean,
								domainMap.bmpMap,
								domainMap.KEY,
								context);
				if (null != domains)
					domainMap.addComponentDomains(domains);
			}
			return domainMap;
		}
	}

	@Deprecated
	public static YATileMapDomain createTileMap(String KEY,
			String jsonFileNameInAsset, Context context)
	{
		return new YHierarchyTileMap(KEY, jsonFileNameInAsset, context);
	}

	/**
	 * 创造一个可破坏的地图实体。目前只支持一图层对应一索引图，只支持一个对象层，注意地图边缘用“多边形”画出，而不是“折线”。
	 * 可以通过destroy方法对地图进行破坏。
	 * 
	 * @param KEY
	 *                实体Key
	 * @param jsonFileNameInAsset
	 *                json文件名
	 * @param context
	 *                上下文
	 * @param world
	 *                box2d世界
	 * @return 地图实体
	 */
	public static YATileMapDomain createDestructibleTerrain(String KEY,
			String jsonFileNameInAsset, Context context, World world)
	{
		return new YDestructibleTerrain(KEY, jsonFileNameInAsset,
				context, world);
	}
}
