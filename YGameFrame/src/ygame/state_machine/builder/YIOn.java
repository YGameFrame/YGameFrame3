package ygame.state_machine.builder;

import ygame.state_machine.YCondition;

public interface YIOn<S, R, C> extends YIWhen<S, R, C> {

	YIWhen<S, R, C> when(YCondition<C> condition);
}
