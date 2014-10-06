package ygame.extension.domain.tilemap;

import java.util.HashMap;
import java.util.Map;

import org.jbox2d.dynamics.World;

import ygame.extension.domain.tilemap.YTiledBean.YTileSet;
import ygame.framework.core.YClusterDomain;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public abstract class YATileMapDomain extends YClusterDomain
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

	// protected final Bitmap getBitmapById(int resId)
	// {
	// return bmpMap.get(resId);
	// }

	protected final Bitmap getBitmapByName(String bitmapName)
	{
		return bmpMap.get(bitmapName);
	}

	public static YATileMapDomain createTileMap(String KEY,
			String jsonFileNameInAsset, Context context)
	{
		return new YHierarchyTileMap(KEY, jsonFileNameInAsset, context);
	}

	/**
	 * 创造一个可破坏的地图实体。目前只支持一图层对应一索引图，只支持一个对象层，注意地图边缘用“多边形”画出，而不是“折线”。
	 * 可以通过destroy方法对地图进行破坏。
	 * @param KEY 实体Key
	 * @param jsonFileNameInAsset json文件名
	 * @param context 上下文
	 * @param world box2d世界
	 * @return 地图实体
	 */
	public static YATileMapDomain createDestructibleTerrain(String KEY,
			String jsonFileNameInAsset, Context context, World world)
	{
		return new YDestructibleTerrain(KEY, jsonFileNameInAsset, context, world);
	}
}
