package me.bscal.betterfarming.common.utils.schedulers;

import java.io.Serializable;
import java.util.function.Consumer;

public interface Schedulable
		extends Consumer<FastRunnableScheduler.FastEntry<?>>, Serializable
{

}
