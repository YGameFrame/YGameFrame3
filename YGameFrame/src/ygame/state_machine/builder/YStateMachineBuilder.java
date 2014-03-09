package ygame.state_machine.builder;

import ygame.state_machine.YIAction;
import ygame.state_machine.YStateMachineTemplate;
import ygame.state_machine.impl.MutableTransitionModelImpl;
import ygame.state_machine.util.ReplacementUtils;

import java.util.List;

/**
 * <b>状态机创建器</b>
 * 
 * <p>
 * <b>概述</b>：用于定制、设计状态机模板
 * 
 * @author 云中
 * 
 * @param <S>
 *            状态类型
 * @param <R>
 *            请求类型
 * @param <C>
 *            上下文类型
 */
public final class YStateMachineBuilder<S, R, C>
{
	private final List<TransitionBuilder<S, R, C>> transitionBuilders = ReplacementUtils
			.newArrayList();
	private final List<EntryExitActionBuilder<S, R, C>> entryExitActions = ReplacementUtils
			.newArrayList();
	private final Class<S> classState;
	private final Class<R> classRequest;

	private YStateMachineBuilder(Class<S> classState, Class<R> classRequest)
	{
		this.classState = classState;
		this.classRequest = classRequest;
	}

	/**
	 * 创建<b>状态机创建器</b>{@link YStateMachineBuilder}
	 * 
	 * @param classState
	 *            状态的类型
	 * @param classRequest
	 *            请求的类型
	 * @return 状态机创建器
	 */
	public static <S, R, C> YStateMachineBuilder<S, R, C> create(Class<S> classState,
			Class<R> classRequest)
	{
		return new YStateMachineBuilder<S, R, C>(classState, classRequest);
	}

	/**
	 * 新建一条转换路径
	 * 
	 * @return 转换路径
	 */
	public YITransition<S, R, C> newTransition()
	{
		TransitionBuilder<S, R, C> transition = new TransitionBuilder<S, R, C>();
		transitionBuilders.add(transition);
		return transition;
	}

	/**
	 * 定制进入某<b>状态</b>时演绎的<b>动作</b>{@link YIAction}
	 * 
	 * @param state
	 *            要定制的状态
	 * @return
	 */
	public YIEntryExit<S, R, C> onEntry(S state)
	{
		EntryExitActionBuilder<S, R, C> actionBuilder = EntryExitActionBuilder
				.entry(state);
		entryExitActions.add(actionBuilder);
		return actionBuilder;
	}

	/**
	 * 定制退出某<b>状态</b>时演绎的<b>动作</b>{@link YIAction}
	 * 
	 * @param state
	 *            要定制的状态
	 * @return
	 */
	public YIEntryExit<S, R, C> onExit(S state)
	{
		EntryExitActionBuilder<S, R, C> actionBuilder = EntryExitActionBuilder
				.exit(state);
		entryExitActions.add(actionBuilder);
		return actionBuilder;
	}

	/**
	 * 构建一个状态转换的流程模板，对于众多状态机，如果它们的状态转换流程相同，
	 * 那么它们可以共享此模板
	 * ，但它们各自的状态是由自己维护的。当你需要数量庞大却拥有相同状态转化流程的状态机时
	 * ，这是一个高效利用内存的方案。
	 * 
	 * 
	 * @return 使用该构建器配置出的状态转换流程模板
	 */
	public YStateMachineTemplate<S, R, C> buildTransitionTemplate()
	{
		//创建空模板
		MutableTransitionModelImpl<S, R, C> template = MutableTransitionModelImpl.create(
				classState, classRequest);

		//根据之前定制的信息填充模板，建立模型
		for (TransitionBuilder<S, R, C> transitionBuilder : transitionBuilders)
			transitionBuilder.addToTransitionModel(template);
		for (EntryExitActionBuilder<S, R, C> entryExitAction : entryExitActions)
			entryExitAction.addToMachine(template);
		return template;
	}
}