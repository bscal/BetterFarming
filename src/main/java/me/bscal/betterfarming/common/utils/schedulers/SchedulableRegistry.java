package me.bscal.betterfarming.common.utils.schedulers;

import java.util.HashMap;
import java.util.Map;

public class SchedulableRegistry
{

	private static final Map<String, Schedulable> PERSISTENT_SCHEDULABLES = new HashMap<>(1);

	public static Schedulable Get(String key)
	{
		return PERSISTENT_SCHEDULABLES.getOrDefault(key, (fastEntry -> System.out.println(
				"[ error ] SchedulableRegistry.GetSchedulable() method defaulted! Key not found! Key = " + key)));
	}

	public static void Put(String key, Schedulable schedulable)
	{
		PERSISTENT_SCHEDULABLES.put(key, schedulable);
	}

	public static boolean Exists(String key)
	{
		return PERSISTENT_SCHEDULABLES.containsKey(key);
	}

}
