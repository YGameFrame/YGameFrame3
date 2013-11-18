package ygame.exception;

import android.util.Log;

public class GetHandleException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GetHandleException(String detailMessage, String strTag,
			String strSuggest)
	{
		super(detailMessage);
		Log.e(strTag, strSuggest);
	}
}
