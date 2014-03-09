package ygame.state_machine;

public interface YCondition<C>
{
	boolean isSatisfied(C context);
}
