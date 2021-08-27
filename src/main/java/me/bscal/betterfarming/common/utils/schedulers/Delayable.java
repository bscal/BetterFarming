package me.bscal.betterfarming.common.utils.schedulers;

import java.io.Serializable;
import java.util.function.Predicate;

public interface Delayable extends Predicate<FastDelayScheduler.DelayEntry>, Serializable
{
}
