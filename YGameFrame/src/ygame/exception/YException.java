package ygame.exception;

import android.util.Log;

public class YException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param strDetailMessage
	 *                异常的详细信息
	 * @param strTag
	 *                异常发生的类
	 * @param strSuggest
	 *                解决或避免该异常的一些建议
	 */
	public YException(String strDetailMessage, String strTag,
			String strSuggest)
	{
		super(strDetailMessage);
		Log.e(strTag, strSuggest + "\n" + strDetailMessage);
	}
}
