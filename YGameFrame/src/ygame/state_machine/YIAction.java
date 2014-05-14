package ygame.state_machine;

public interface YIAction<S, R, C>
{
	/**
	 * 当状态转化发生时，该<b>动作</b>被演绎
	 * 
	 * @param from
	 *            转换前的状态
	 * @param to
	 *            转换后的状态
	 * @param causedBy
	 *            引发转换的请求
	 * @param context
	 *            上下文
	 * @param stateMachine
	 *            状态机
	 */
	void onTransition(S from, S to, R causedBy, C context,
			StateMachine<S, R, C> stateMachine);
}
