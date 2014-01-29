package ygame.framework.core;

import ygame.framework.request.YRequest;

/**
 * <b>实体请求</b>
 * 
 * <p>
 * <b>概述</b>： 对实体发出的请求
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>： TODO
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public class YDomainRequest extends YRequest
{
	final String WHO;

	/**
	 * @param KEY
	 *                请求的标识
	 * @param WHO
	 *                请求接收者的标识
	 *                {@link YABaseDomain#KEY}
	 */
	public YDomainRequest(int KEY, String WHO , YWhen when)
	{
		super(KEY , when);
		this.WHO = WHO;
	}
}
