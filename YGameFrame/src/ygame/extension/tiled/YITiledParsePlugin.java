package ygame.extension.tiled;

import android.content.res.Resources;

public interface YITiledParsePlugin
{
	void parse(YTiled tiled, float fPixelsPerUnit,
			Resources resources);
}
