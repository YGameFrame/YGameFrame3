package ygame.extension.domain.tilemap;

import java.util.HashMap;
import java.util.Map;

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
			int resId = resources.getIdentifier(drawableName, "drawable",
					context.getPackageName());
			Options options = new Options();
			options.inScaled = false;
			bmpMap.put(drawableName, BitmapFactory.decodeResource(
					resources, resId, options));
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

}
