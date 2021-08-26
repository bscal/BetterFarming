package me.bscal.betterfarming.common.utils.schedulers.example;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.schedulers.FastRunnableScheduler;
import me.bscal.betterfarming.common.utils.schedulers.PersistentSchedulable;

public class TestPersistentSchedulable
{

	public static PersistentSchedulable Function()
	{
		return new PersistentSchedulable()
		{
			@Override
			public void accept(FastRunnableScheduler.FastEntry fastEntry)
			{
				BetterFarming.LOGGER.info("working 2");
			}
		};

	}

}
