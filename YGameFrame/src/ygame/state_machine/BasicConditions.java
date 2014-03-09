package ygame.state_machine;

import java.util.List;

@Deprecated
public class BasicConditions
{

	private BasicConditions()
	{
	}

	public static <C> YCondition<C> always()
	{
		return new YCondition<C>()
		{
			@Override
			public boolean isSatisfied(C context)
			{
				return true;
			}
		};
	}

	public static <C> YCondition<C> and(final YCondition<C> first, final YCondition<C> second)
	{
		return new YCondition<C>()
		{
			@Override
			public boolean isSatisfied(C context)
			{
				return first.isSatisfied(context) && second.isSatisfied(context);
			}
		};
	}

	public static <C> YCondition<C> and(final List<YCondition<C>> conditions)
	{
		return new YCondition<C>()
		{
			@Override
			public boolean isSatisfied(C context)
			{
				for (YCondition<C> condition : conditions)
				{
					if (!condition.isSatisfied(context))
					{
						return false;
					}
				}
				return true;
			}
		};
	}

	public static <C> YCondition<C> or(final YCondition<C> first, final YCondition<C> second)
	{
		return new YCondition<C>()
		{
			@Override
			public boolean isSatisfied(C context)
			{
				return first.isSatisfied(context) || second.isSatisfied(context);
			}
		};
	}

	public static <C> YCondition<C> or(final List<YCondition<C>> conditions)
	{
		return new YCondition<C>()
		{
			@Override
			public boolean isSatisfied(C context)
			{
				for (YCondition<C> condition : conditions)
				{
					if (condition.isSatisfied(context))
					{
						return true;
					}
				}
				return false;
			}
		};
	}

	public static <C> YCondition<C> not(final YCondition<C> condition)
	{
		return new YCondition<C>()
		{
			@Override
			public boolean isSatisfied(C context)
			{
				return !condition.isSatisfied(context);
			}
		};
	}

	public static <C> YCondition<C> xor(final YCondition<C> first, final YCondition<C> second)
	{
		return new YCondition<C>()
		{
			@Override
			public boolean isSatisfied(C context)
			{
				return first.isSatisfied(context) ^ second.isSatisfied(context);
			}
		};
	}
}