package ygame.utils;

import android.util.Log;

public final class YLog
{
	private YLog()
	{
	}

	public static void w(String tag, String msg)
	{
		Log.w(tag, msg);
	}

	public static void i(String tag, String msg)
	{
		Log.i(tag, msg);
	}
}