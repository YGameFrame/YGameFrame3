package ygame.extension.domain.tilemap;

import java.util.HashMap;
import java.util.Map;

import ygame.extension.domain.tilemap.YTiledBean.YTileSet;
import ygame.framework.core.YClusterDomain;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class YATileMapDomain extends YClusterDomain
{
	protected final YTiledBean tiledBean;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Bitmap> bmpMap = new HashMap<Integer, Bitmap>();

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
			String name = tileSet.getName();
			int resId = resources.getIdentifier(name, "drawable",
					context.getPackageName());
			bmpMap.put(resId, BitmapFactory.decodeResource(
					resources, resId));
		}
	}

	protected final Bitmap getBitmapById(int resId)
	{
		return bmpMap.get(resId);
	}

}
