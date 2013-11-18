package ygame.framework;

import android.util.Log;

/**
 * 当手机不支持OpenGL_ES2.0时抛出该异常
 * 
 * @author yunzhong
 * 
 */
public final class YUnsupportGL2_0Exception extends Exception
{
	private static final long serialVersionUID = 8219001268520830893L;

	public YUnsupportGL2_0Exception(String strTAG)
	{
		super("手机不支持OpenGL_ES2.0");
		Log.e(strTAG, "手机不支持OpenGL_ES2.0");
	}
}