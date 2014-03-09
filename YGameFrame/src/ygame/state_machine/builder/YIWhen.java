package ygame.state_machine.builder;

import ygame.state_machine.YIAction;

import java.util.List;

public interface YIWhen<S, R, C> {
	void perform(YIAction<S, R, C> action);

	void perform(List<YIAction<S, R, C>> actions);
}
