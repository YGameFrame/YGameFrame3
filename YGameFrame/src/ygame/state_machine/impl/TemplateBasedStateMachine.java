package ygame.state_machine.impl;

import ygame.state_machine.StateMachine;
import ygame.state_machine.TransitionModel;


class TemplateBasedStateMachine<S, R, C> implements StateMachine<S, R, C>
{
	private final TransitionModel<S, R, C> model;
	private S currentState;
	private C context;

	public TemplateBasedStateMachine(TransitionModel<S, R, C> model, S initial, C context)
	{
		if (initial == null)
		{
			throw new IllegalArgumentException("Initial state must not be null");
		}
		this.model = model;
		currentState = initial;
		this.context = context;
	}

	@Override
	public S getCurrentState()
	{
		return currentState;
	}

	@Override
	public boolean inputRequest(R request)
	{
		return model.inputRequest(this, request, context);
	}

	@Override
	public void rawSetState(S rawState)
	{
		currentState = rawState;
	}

	@Override
	public boolean forceSetState(S forcedState)
	{
		return model.forceSetState(this, forcedState , context);
	}
}
