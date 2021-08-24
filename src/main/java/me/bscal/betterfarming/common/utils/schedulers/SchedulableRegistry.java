package me.bscal.betterfarming.common.utils.schedulers;

import me.bscal.betterfarming.BetterFarming;

import java.util.function.Consumer;

public class SchedulableRegistry
{

	public static Schedulable TestRunnable()
	{
		return (entry) -> {
			if (entry instanceof FastRunnableScheduler.BlockEntry blockEntry)
				BetterFarming.LOGGER.info("HELLO " + blockEntry.world.getRegistryKey());
		};
	}

}
