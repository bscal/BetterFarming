package me.bscal.betterfarming.common.utils.schedulers;

import java.lang.ref.WeakReference;
import java.util.PriorityQueue;
import java.util.Queue;

public class FastDelayScheduler
{

	private final Queue<DelayEntry> entries;
	private int lastProcessedTick = -1;

	public FastDelayScheduler()
	{
		entries = new PriorityQueue<>(((o1, o2) -> {
			if (o1.dueAtTick < o2.dueAtTick)
				return 1;
			else if (o1.dueAtTick > o2.dueAtTick)
				return -1;
			return 0;
		}));
	}

	public void Tick(int currentTick)
	{
		lastProcessedTick = currentTick;
		while (entries.peek() != null)
		{
			var entry = entries.remove();
			if (entry.dueAtTick > currentTick)
				return;
			boolean shouldReschedule = DoRunnable(entry);
			if (entry.repeat && shouldReschedule)
			{
				ScheduleEntry(new DelayEntry(entry.runnable, entry.interval, entry.owner, currentTick + entry.interval, true));
			}
		}
	}

	public void ScheduleRunnable(Runnable runnable, int dueInTicks, Object owner, boolean repeat)
	{
		if (lastProcessedTick < 0) return;
		if (runnable == null) return;
		if (dueInTicks < 0) return;
		if (dueInTicks == 0 && repeat) return;
		DelayEntry entry = new DelayEntry(runnable, dueInTicks, new WeakReference<>(owner), lastProcessedTick + dueInTicks, repeat);
		if (dueInTicks == 0)
			DoRunnable(entry);
		else
			ScheduleEntry(entry);
	}

	private void ScheduleEntry(DelayEntry delayEntry)
	{
		entries.add(delayEntry);
	}

	private boolean DoRunnable(DelayEntry entry)
	{
		if (entry.owner.get() != null)
		{
			entry.runnable.run();
			return true;
		}
		return false;
	}

	public static record DelayEntry(Runnable runnable, int interval, WeakReference<Object> owner, int dueAtTick, boolean repeat)
	{
	}

}
