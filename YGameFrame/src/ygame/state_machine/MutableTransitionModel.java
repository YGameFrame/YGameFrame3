package ygame.state_machine;

import java.util.List;

public interface MutableTransitionModel<S, R, C> extends TransitionModel<S, R, C>,
		YStateMachineTemplate<S, R, C>
{

	void addTransition(S from, S to, R request, YCondition<C> condition,
			List<YIAction<S, R, C>> actions);

	void addFromAllTransition(S to, R request, YCondition<C> condition,
			List<YIAction<S, R, C>> actions);

	void addEntryAction(S entryState, YIAction<S, R, C> action);

	void addExitAction(S exitState, YIAction<S, R, C> action);
}
