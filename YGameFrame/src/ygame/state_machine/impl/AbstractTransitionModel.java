package ygame.state_machine.impl;

import ygame.state_machine.StateMachine;
import ygame.state_machine.TransitionModel;
import ygame.state_machine.YIAction;
import ygame.state_machine.util.ReplacementUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

abstract class AbstractTransitionModel<S, R, C> implements
		TransitionModel<S, R, C>
{
	protected final Map<S, Map<R, Collection<Transition<S, R, C>>>> transitionMap;
	protected final Map<R, Collection<Transition<S, R, C>>> fromAllTransitions;
	protected final Map<S, Collection<YIAction<S, R, C>>> exitActions;
	protected final Map<S, Collection<YIAction<S, R, C>>> enterActions;
	protected final Class<S> classState;
	protected final Class<R> classRequest;

	protected AbstractTransitionModel(Class<S> classState, Class<R> classRequest)
	{
		this.classState = classState;
		this.classRequest = classRequest;
		transitionMap = createMap(classState);
		exitActions = createMap(classState);
		enterActions = createMap(classState);
		fromAllTransitions = createMap(classRequest);
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	protected static <S, T> Map<S, T> createMap(Class<S> state)
	{
		if (state.isEnum())
			return new EnumMap(state);
		else
			return ReplacementUtils.newHashMap();
	}

	@Override
	public boolean inputRequest(StateMachine<S, R, C> stateMachine, R request, C context)
	{
		S from = stateMachine.getCurrentState();
		return inputRequest(stateMachine, request, transitionMap.get(from), from, context)
				|| inputRequest(stateMachine, request, fromAllTransitions, from, context);
	}

	@Override
	public boolean forceSetState(StateMachine<S, R, C> stateMachine, S forcedState)
	{
		S from = stateMachine.getCurrentState();
		if (from.equals(forcedState))
			return false;
		forceSetState(stateMachine, from, forcedState, null, null, null);
		return true;
	}

	private void forceSetState(StateMachine<S, R, C> stateMachine, S from, S to,
			Transition<S, R, C> transition, R request, C context)
	{
		invoke(exitActions.get(from), from, to, request, context, stateMachine);
		stateMachine.rawSetState(to);
		if (transition != null)
			transition.onTransition(from, to, request, context, stateMachine);
		invoke(enterActions.get(to), from, to, request, context, stateMachine);
	}

	private boolean inputRequest(StateMachine<S, R, C> stateMachine, R request,
			Map<R, Collection<Transition<S, R, C>>> transitionMap, S from, C context)
	{
		if (transitionMap == null)
			return false;
		Collection<Transition<S, R, C>> transitions = transitionMap.get(request);
		if (transitions == null)
			return false;
		for (Transition<S, R, C> transition : transitions)
			if (transition.isSatisfied(context))
			{
				forceSetState(stateMachine, from, transition.getTo(), transition,
						request, context);
				return true;
			}
		return false;
	}

	private void invoke(Collection<YIAction<S, R, C>> actions, S from, S to, R request,
			C context, StateMachine<S, R, C> stateMachine)
	{
		if (actions == null)
			return;
		for (YIAction<S, R, C> action : actions)
			action.onTransition(from, to, request, context, stateMachine);
	}

	public Map<S, Map<R, Collection<Transition<S, R, C>>>> getStateTransitions()
	{
		return Collections.unmodifiableMap(transitionMap);
	}

	public Map<R, Collection<Transition<S, R, C>>> getFromAllTransitions()
	{
		return Collections.unmodifiableMap(fromAllTransitions);
	}

}