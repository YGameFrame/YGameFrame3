package ygame.state_machine.builder;

import ygame.state_machine.MutableTransitionModel;
import ygame.state_machine.YIAction;
import ygame.state_machine.util.ReplacementUtils;

import java.util.List;

//隐
final class EntryExitActionBuilder<S, R, C> implements YIEntryExit<S, R, C>
{
	private final Mode mode;
	private final S state;

	private EntryExitActionBuilder(Mode mode, S state)
	{
		this.mode = mode;
		this.state = state;
	}

	private enum Mode
	{
		ENTRY, EXIT
	}

	private final List<YIAction<S, R, C>> actions = ReplacementUtils.newArrayList();

	//隐
	static <S, R, C> EntryExitActionBuilder<S, R, C> entry(S to)
	{
		return new EntryExitActionBuilder<S, R, C>(Mode.ENTRY, to);
	}

	//隐
	static <S, R, C> EntryExitActionBuilder<S, R, C> exit(S from)
	{
		return new EntryExitActionBuilder<S, R, C>(Mode.EXIT, from);
	}

	@Override
	public YIEntryExit<S, R, C> perform(YIAction<S, R, C> action)
	{
		this.actions.add(action);
		return this;
	}

	@Override
	public YIEntryExit<S, R, C> perform(List<YIAction<S, R, C>> actions)
	{
		this.actions.addAll(actions);
		return this;
	}

	//隐
	void addToMachine(MutableTransitionModel<S, R, C> machineConstructor)
	{
		for (YIAction<S, R, C> action : actions)
			add(machineConstructor, action);
	}

	private void add(MutableTransitionModel<S, R, C> machineConstructor,
			YIAction<S, R, C> action)
	{
		switch (mode)
		{
		case ENTRY:
			machineConstructor.addEntryAction(state, action);
			break;
		case EXIT:
			machineConstructor.addExitAction(state, action);
			break;
		}
	}
}