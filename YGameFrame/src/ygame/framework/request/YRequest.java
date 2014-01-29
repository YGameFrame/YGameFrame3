package ygame.framework.request;

public class YRequest
{
	/** <b>请求标识</b>：区分不同的请求 */
	public final int iKEY;
	/** <b>请求处理时段</b>：指定请求在何时被处理，详见{@link YWhen} */
	public final YWhen WHEN;

	public YRequest(int iKEY, YWhen when)
	{
		this.iKEY = iKEY;
		this.WHEN = when;
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
		/** <b>渲染前于逻辑线程中</b> */
		BEFORE_RENDER, /** <b>渲染时于逻辑线程</b> */
		RENDERING_EXE_IN_LOGIC_THREAD;

	}
}
