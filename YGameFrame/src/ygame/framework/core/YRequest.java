package ygame.framework.core;


/**
 * <b>请求</b>
 * 
 * <p>
 * <b>概述</b>： TODO
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>： 如果请求键值{@link #iKEY}一样则视为相同请求
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public class YRequest
{
	/** <b>请求标识</b>：区分不同的请求 */
	public final int iKEY;
	/** <b>请求处理时段</b>：指定请求在何时被处理，详见{@link YWhen} */
	final YWhen WHEN_TO_DEAL;

	private final int iHashCode;

//	/** <b>必须对该变量赋值！！！</b> */
//	YAStateMachineContext target;

	public YRequest(int iKEY, YWhen whenToDeal)
	{
		this.iKEY = iKEY;
		this.WHEN_TO_DEAL = whenToDeal;
		iHashCode = calculateHashCode();
	}

	/**
	 * 此构造子构造的请求将在渲染时进行处理
	 * 
	 * @param iKEY
	 *                请求键值
	 */
	public YRequest(int iKEY)
	{
		this(iKEY, YWhen.RENDERING_EXE_IN_LOGIC_THREAD);
	}

	private int calculateHashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + iKEY;
		return result;
	}

	@Override
	final public int hashCode()
	{
		return iHashCode;
	}

	@Override
	final public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		YRequest other = (YRequest) obj;
		if (iKEY != other.iKEY)
			return false;
		return true;
	}

	/**
	 * <b>请求处理时机</b>
	 * 
	 * <p>
	 * <b>概述</b>： 指定请求在何时、在什么线程中被处理
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
	public static enum YWhen
	{
		//@formatter:off
		/** <b>渲染前于逻辑线程中</b> */
		BEFORE_RENDER, 
		/** <b>渲染时于逻辑线程</b> */
		RENDERING_EXE_IN_LOGIC_THREAD;
		//@formatter:on
	}
}
