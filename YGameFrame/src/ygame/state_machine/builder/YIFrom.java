package ygame.state_machine.builder;

public interface YIFrom<S, R, C>
{
	YITo<S, R, C> to(S toState);
}
