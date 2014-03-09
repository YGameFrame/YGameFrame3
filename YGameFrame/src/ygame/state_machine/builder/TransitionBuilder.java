package ygame.state_machine.builder;

import ygame.state_machine.BasicConditions;
import ygame.state_machine.MutableTransitionModel;
import ygame.state_machine.YCondition;
import ygame.state_machine.YIAction;
import ygame.state_machine.util.ReplacementUtils;

import java.util.List;

@SuppressWarnings("deprecation")
class TransitionBuilder<S, R, C> implements YITransition<S, R, C>, YIFrom<S, R, C>,
		YITo<S, R, C>, YIOn<S, R, C>, YIWhen<S, R, C>
{
	private S from;
	private S to;
	private R request;
	private YCondition<C> condition = BasicConditions.always();
	private final List<YIAction<S, R, C>> actions = ReplacementUtils.newArrayList();

	public YIOn<S, R, C> on(R request)
	{
		this.request = request;
		return this;
	}

	@Override
	public YIFrom<S, R, C> from(S fromState)
	{
		this.from = fromState;
		return this;
	}

	@Override
	public YIFrom<S, R, C> fromAll()
	{
		this.from = null;
		return this;
	}

	@Override
	public YITo<S, R, C> to(S toState)
	{
		this.to = toState;
		return this;
	}

	public YIWhen<S, R, C> when(YCondition<C> condition)
	{
		this.condition = condition;
		return this;
	}

	public void perform(List<YIAction<S, R, C>> actions)
	{
		this.actions.addAll(actions);
	}

	public void perform(YIAction<S, R, C> action)
	{
		this.actions.add(action);
	}

	void addToTransitionModel(MutableTransitionModel<S, R, C> transitionModel)
	{
		if (request == null)
		{
			String fromString = from == null ? "anyState" : from.toString();
			handleMissingOn(fromString, to.toString());
		}
		if (from == null)
		{
			transitionModel.addFromAllTransition(to, request, (YCondition<C>) condition,
					actions);
		} else
		{
			transitionModel.addTransition(from, to, request, (YCondition<C>) condition,
					actions);
		}
	}

	private void handleMissingOn(String from, String to)
	{
		throw new IllegalStateException(from + " -> " + to + "完成，但尚未指定需要的请求，使用on()指定");
	}
}