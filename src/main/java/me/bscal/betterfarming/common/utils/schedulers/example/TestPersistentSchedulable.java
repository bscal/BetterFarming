package me.bscal.betterfarming.common.utils.schedulers.example;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.schedulers.FastIntervalScheduler;
import me.bscal.betterfarming.common.utils.schedulers.PersistentSchedulable;

public class TestPersistentSchedulable
{

	public static PersistentSchedulable Function()
	{
		return new PersistentSchedulable()
		{
			@Override
			public void accept(FastIntervalScheduler.FastEntry fastEntry)
			{
				BetterFarming.LOGGER.info("working 2");
			}
		};

	}

}
