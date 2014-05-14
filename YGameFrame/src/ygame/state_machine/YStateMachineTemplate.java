package ygame.state_machine;

public interface YStateMachineTemplate<S, R, C>
{

	/**
	 * 创建一个利用该模板的<b>状态机</b>
	 * 
	 * @param init
	 *            状态机的起始状态
	 * @param context
	 *            状态机上下文
	 * @return 状态机
	 */
	StateMachine<S, R, C> newStateMachine(S init, C context);
}
