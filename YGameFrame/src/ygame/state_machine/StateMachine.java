package ygame.state_machine;

/**
 * 向用户提供的一个状态机调用界面，此状态机已被定制完毕，不可再行设置
 * 
 * @param <S>
 *            状态类型
 * @param <R>
 *            请求类型
 * @param <C>
 *            上下文
 */
public interface StateMachine<S, R, C>
{
	/**
	 * 获取当前状态
	 * 
	 * @return 当前状态
	 */
	S getCurrentState();

	/**
	 * 向<b>状态机</b>输入一个<b>请求</b>,这可能会引发一个状态转换
	 * 
	 * @param request
	 *            输入的请求
	 * @return 引发了状态转换返回真，反之为假
	 */
	boolean inputRequest(R request);

	/**
	 * 将状态机的当前状态强制设置为新状态，这不会导致任何<b>状态进入动作</b>、<b>
	 * 状态离开动作</b>、<b>过渡动作</b>的回调
	 * 
	 * @param rawState
	 *            要设置的新状态
	 */
	void rawSetState(S rawState);

	/**
	 * 强制状态机进入新状态，这不会导致任何<b>过渡动作</b>的回调，但会导致<b>当前状态退出
	 * </b>与<b>新状态进入</b>的回调
	 * 
	 * 但是退出当前状态和行为
	 * 
	 * 输入操作在新状态运行
	 * 
	 * 
	 * @param forcedState
	 *            要设置的新状态
	 * @return 成功改变状态返回真，反之假
	 */
	boolean forceSetState(S forcedState);
}
