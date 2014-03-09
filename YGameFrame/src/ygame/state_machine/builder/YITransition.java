package ygame.state_machine.builder;

public interface YITransition<S, R, C>
{
	YIFrom<S, R, C> from(S fromState);

	YIFrom<S, R, C> fromAll();
}
