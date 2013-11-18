package ygame.framework;

import ygame.framework.request.YRequest;

/**
 * <b>状态机上下文</b>
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
 * <b>注</b>： TODO
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public abstract class YAStateMachineContext
{
	/**
	 * 向状态机输入<b>请求</b>{@link YRequest}
	 * 
	 * @param request
	 *                请求
	 */
	abstract protected void inputRequest(YRequest request);

	// abstract protected void
	// onClockCycle(double dbElapseTime_s);
}
