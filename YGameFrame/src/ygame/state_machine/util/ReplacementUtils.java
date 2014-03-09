package ygame.state_machine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplacementUtils
{
	private ReplacementUtils()
	{
	}

	public static <T> List<T> newArrayList()
	{
		return new ArrayList<T>();
	}

	public static <K, V> Map<K, V> newHashMap()
	{
		return new HashMap<K, V>();
	}
}
