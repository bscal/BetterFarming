package me.bscal.betterfarming.common.utils;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Inspiration from HugsLib's tick scheduler a Rimworld modding library. Thought it could be a useful addition when I want to
 * scheduler runnables for mods or plugins more efficiently
 */
public class FastRunnable
{

	private final List<FastListRunnable> runnables;

	public FastRunnable()
	{
		runnables = new ArrayList<>();
	}

	private static class FastListRunnable
	{
		public final int tickInterval;
		private final List<FastEntry> tickEntries;
		private final FastRunnable scheduler;
		private int currentIndex;
		private float listProgress;
		private int nextCycleStart;
		private int numCalls;
		private boolean tickInProgress;

		public FastListRunnable(int tickInterval, FastRunnable scheduler)
		{
			this.tickInterval = tickInterval;
			this.tickEntries = new ArrayList<>();
			this.scheduler = scheduler;
		}

		public void Tick(int currentTick)
		{
			tickInProgress = true;
			numCalls = 0;
			if (nextCycleStart <= currentTick)
			{
				currentIndex = 0;
				listProgress = 0;
				nextCycleStart = currentTick + tickInterval;
			}
			listProgress += tickEntries.size() / (float) tickInterval;
			var maxIndex = Math.min(tickEntries.size(), Math.ceil(listProgress));
			var it = tickEntries.listIterator();
			while (it.hasNext())
			{
				var entry = it.next();
				if (entry.IsValid())
				{
					entry.OnTick();
					numCalls++;
				}
				else
				{
					it.remove();
				}
				currentTick++;
			}
			tickInProgress = false;
		}

	}

	private abstract static class FastEntry<T>
	{

		public final T owner;
		public final int interval;
		public final Runnable runnable;

		public FastEntry(T owner, int interval, Runnable runnable)
		{
			this.owner = owner;
			this.interval = interval;
			this.runnable = runnable;
		}

		public abstract boolean IsValid();

		public abstract void OnTick();
	}

	private static class BlockEntry extends FastEntry<BlockPos>
	{

		public final ServerWorld world;

		public BlockEntry(BlockPos owner, int interval, Runnable runnable, ServerWorld world)
		{
			super(owner, interval, runnable);
			this.world = world;
		}

		@Override
		public boolean IsValid()
		{
			return world.isPosLoaded(owner.getX(), owner.getZ());
		}

		@Override
		public void OnTick()
		{
			runnable.run();
		}
	}

	private static class EntityEntry extends FastEntry<Entity>
	{

		public EntityEntry(Entity owner, int interval,  Runnable runnable)
		{
			super(owner, interval, runnable);
		}

		@Override
		public boolean IsValid()
		{
			return owner.isAlive();
		}

		@Override
		public void OnTick()
		{
		}
	}

}
