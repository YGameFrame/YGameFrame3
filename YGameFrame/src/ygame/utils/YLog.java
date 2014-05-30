package ygame.utils;

import android.util.Log;

public final class YLog
{
	public static final boolean bDebug = true;

	private YLog()
	{
	}

	public static void w(String tag, String msg)
	{
		if (bDebug)
			Log.w(tag, msg);
	}

	public static void i(String tag, String msg)
	{
		if (bDebug)
			Log.i(tag, msg);
	}
}
