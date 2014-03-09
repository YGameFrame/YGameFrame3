package ygame.state_machine.impl;

import ygame.state_machine.StateMachine;
import ygame.state_machine.YCondition;
import ygame.state_machine.YIAction;
import ygame.state_machine.util.ReplacementUtils;

import java.util.Collection;

public final class Transition<S, R, C>
{
	private final S to;
	private final YCondition<C> condition;
	private final Collection<YIAction<S, R, C>> actions = ReplacementUtils.newArrayList();

	public Transition(S to, YCondition<C> condition, Collection<YIAction<S, R, C>> actions)
	{
		this.to = to;
		this.condition = condition;
		this.actions.addAll(actions);
	}

	public S getTo()
	{
		return to;
	}

	public boolean isSatisfied(C context)
	{
		return condition.isSatisfied(context);
	}

	public YCondition<C> getCondition()
	{
		return condition;
	}

	public void onTransition(S from, S to, R request, C context,
			StateMachine<S, R, C> statemachine)
	{
		for (YIAction<S, R, C> action : actions)
			action.onTransition(from, to, request, context, statemachine);
	}
}
