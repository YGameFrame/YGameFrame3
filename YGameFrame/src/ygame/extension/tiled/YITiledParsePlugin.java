package ygame.extension.tiled;

import java.util.Collection;

import ygame.framework.core.YABaseDomain;
import android.content.res.Resources;

public interface YITiledParsePlugin
{
	Collection<YABaseDomain> parse(YTiled tiled, float fPixelsPerUnit,
			Resources resources);
}
