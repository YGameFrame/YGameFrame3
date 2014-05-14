package ygame.state_machine.builder;

import ygame.state_machine.YIAction;

import java.util.List;

/**
 * <b>进入状态或离开状态</b>
 * 
 * <p>
 * <b>概述</b>：为指定状态定制离开或进入时演绎的动作
 * <p>
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
public interface YIEntryExit<S, R, C>
{
	YIEntryExit<S, R, C> perform(YIAction<S, R, C> action);

	YIEntryExit<S, R, C> perform(List<YIAction<S, R, C>> actions);
}
