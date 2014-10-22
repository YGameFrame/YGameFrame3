package ygame.exception;

import android.util.Log;

public class YException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String detailMsg;
	private final String tag;
	private final String suggest;

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
		this.detailMsg = strDetailMessage;
		this.tag = strTag;
		this.suggest = strSuggest;
	}

	public YException(YException e)
	{
		this(e.detailMsg, e.tag, e.suggest);
	}

	public YException(Exception e)
	{
		super(e);
		this.detailMsg = "";
		this.suggest = "";
		this.tag = "";
		e.printStackTrace();
	}
}
