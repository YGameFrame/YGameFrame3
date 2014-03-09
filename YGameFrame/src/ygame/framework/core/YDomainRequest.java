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
 * <b>注</b>：两个请求的标识与发送对象相同时则视为同一个请求
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
	private final int iHashCode;

	/**
	 * @param KEY
	 *                请求的标识
	 * @param WHO
	 *                请求接收者的标识 {@link YABaseDomain#KEY}
	 */
	public YDomainRequest(int KEY, String WHO, YWhen when)
	{
		super(KEY, when);
		this.WHO = WHO;
		iHashCode = calculateHashCode();
	}

	private int calculateHashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((WHO == null) ? 0 : WHO.hashCode());
		return result;
	}

	@Override
	public int hashCode()
	{
		return iHashCode;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		YDomainRequest other = (YDomainRequest) obj;
		if (WHO == null)
		{
			if (other.WHO != null)
				return false;
		} else if (!WHO.equals(other.WHO))
			return false;
		return true;
	}
}
