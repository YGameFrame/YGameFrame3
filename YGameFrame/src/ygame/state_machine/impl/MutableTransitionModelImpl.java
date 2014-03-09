package ygame.state_machine.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ygame.state_machine.MutableTransitionModel;
import ygame.state_machine.StateMachine;
import ygame.state_machine.YCondition;
import ygame.state_machine.YIAction;
import ygame.state_machine.util.ReplacementUtils;

public class MutableTransitionModelImpl<S, R, C> extends AbstractTransitionModel<S, R, C>
		implements MutableTransitionModel<S, R, C>
{

	private MutableTransitionModelImpl(Class<S> classState, Class<R> classRequest)
	{
		super(classState, classRequest);
	}

	public static <S, R, C> MutableTransitionModelImpl<S, R, C> create(
			Class<S> classState, Class<R> classRequest)
	{
		return new MutableTransitionModelImpl<S, R, C>(classState, classRequest);
	}

	@Override
	public StateMachine<S, R, C> newStateMachine(S init, C context)
	{
		return new TemplateBasedStateMachine<S, R, C>(this, init, context);
	}

	@Override
	public void addTransition(S from, S to, R request, YCondition<C> condition,
			List<YIAction<S, R, C>> actions)
	{
		Map<R, Collection<Transition<S, R, C>>> map = transitionMap.get(from);
		if (map == null)
		{
			map = createMap(classRequest);
			transitionMap.put(from, map);
		}
		Collection<Transition<S, R, C>> transitions = map.get(request);
		if (transitions == null)
		{
			transitions = ReplacementUtils.newArrayList();
			map.put(request, transitions);
		}
		transitions.add(new Transition<S, R, C>(to, condition, actions));
	}

	@Override
	public void addFromAllTransition(S to, R request, YCondition<C> condition,
			List<YIAction<S, R, C>> actions)
	{
		Collection<Transition<S, R, C>> transitions = fromAllTransitions.get(request);
		if (transitions == null)
		{
			transitions = ReplacementUtils.newArrayList();
			fromAllTransitions.put(request, transitions);
		}
		transitions.add(new Transition<S, R, C>(to, condition, actions));
	}

	@Override
	public void addEntryAction(S entryState, YIAction<S, R, C> action)
	{
		addAction(entryState, action, enterActions);
	}

	private void addAction(S entryState, YIAction<S, R, C> action,
			Map<S, Collection<YIAction<S, R, C>>> map)
	{
		Collection<YIAction<S, R, C>> collection = map.get(entryState);
		if (collection == null)
		{
			collection = ReplacementUtils.newArrayList();
			map.put(entryState, collection);
		}
		collection.add(action);
	}

	@Override
	public void addExitAction(S exitState, YIAction<S, R, C> action)
	{
		addAction(exitState, action, exitActions);
	}

}
