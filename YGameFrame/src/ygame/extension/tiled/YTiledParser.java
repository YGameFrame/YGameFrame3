package ygame.extension.tiled;

import java.util.ArrayList;
import java.util.List;

import ygame.framework.core.YScene;
import android.content.Context;

public final class YTiledParser
{
	private List<YITiledParsePlugin> parsePlugins = new ArrayList<YITiledParsePlugin>();
	private YTiled tiled;

	public YTiledParser(YScene scene, String jsonFileNameInAssert,
			Context context)
	{
		tiled = new YTiled(scene, jsonFileNameInAssert, context);
	}

	public YTiledParser append(YITiledParsePlugin plugin)
	{
		parsePlugins.add(plugin);
		return this;
	}

	public void parse()
	{
		tiled.parse(parsePlugins);
	}

}
