package ygame.framework.core;

import ygame.framework.request.YRequest;

/**
 * <b>实体逻辑请求</b>
 * 
 * <p>
 * <b>概述</b>： 对实体逻辑发出的请求
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

	final int iWHO;
	final YABaseDomain DOMAIN;

	/**
	 * @param iKEY
	 *                请求的标识
	 * @param iWHO
	 *                请求接收者的标识
	 *                {@link YABaseDomain#iKEY}
	 */
	public YDomainRequest(int iKEY, int iWHO)
	{
		super(iKEY);
		this.DOMAIN = null;
		this.iWHO = iWHO;
	}

	/**
	 * @param iKEY
	 *                请求标识
	 * @param domain
	 *                请求接收者
	 */
	public YDomainRequest(int iKEY, YABaseDomain domain)
	{
		super(iKEY);
		this.DOMAIN = domain;
		this.iWHO = Integer.MAX_VALUE;
	}

}
