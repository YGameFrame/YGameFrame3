package ygame.extension.domain.tilemap.parse_plugin;

import java.util.Collection;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import ygame.extension.domain.tilemap.YTiledBean;
import ygame.framework.core.YABaseDomain;

public interface YITileMapParsePlugin
{
	Collection<YABaseDomain> parse(YTiledBean tiledBean,
			Map<String, Bitmap> indexPictures, String key,
			Context context);
}
