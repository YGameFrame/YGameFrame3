package ygame.state_machine.builder;

public interface YITo<S, R, C>
{
	YIOn<S, R, C> on(R request);
}
