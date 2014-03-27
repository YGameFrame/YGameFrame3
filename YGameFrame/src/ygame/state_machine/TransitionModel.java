package ygame.state_machine;

public interface TransitionModel<S, R, C>
{

	/**
	 * 向<b>状态机</b>输入<b>请求</b>，或将改变 <b>状态机</b>的<b>状态</b>，并演绎<b>过渡动作</b>
	 * 
	 * @param machine
	 *                输入请求的状态机
	 * @param request
	 *                输入的请求
	 * @param context
	 *                向状态机提供的上下文
	 * @return true 真如果状态转换成功，反之假。<b>值得注意的是，</b>一个向本状态的
	 *         <b>转换</b>转换成功时，该函数返回为真
	 */
	boolean inputRequest(StateMachine<S, R, C> machine, R request, C context);

	/**
	 * 
	 * 强制设置新状态， 不会回调<b>过渡动作</b>,但会触发<b>当前状态离开</b>与<b>新状态进入 </b>动作的回调
	 * 
	 * @param stateMachine
	 *                要被设置新状态的状态机
	 * @param forcedState
	 *                要设置的新状态
	 * @param context
	 *                上下文
	 * @return 真如果转换成功，反之为假
	 */
	boolean forceSetState(StateMachine<S, R, C> stateMachine,
			S forcedState, C context);

}
