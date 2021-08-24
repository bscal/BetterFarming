package me.bscal.betterfarming.common.utils.schedulers;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Inspiration from HugsLib's tick scheduler a Rimworld modding library. Thought it could be a useful addition when I want to
 * scheduler runnables for mods or plugins more efficiently
 */
public class FastRunnableScheduler
{

	private final List<FastRunnableList> runnableLists;

	public FastRunnableScheduler()
	{
		runnableLists = new ArrayList<>();
	}

	public void RegisterRunnable(FastEntry<?> entry)
	{
		if (!entry.IsValid())
		{
			BetterFarming.LOGGER.error("Trying to register an invalid owner!");
			return;
		}
		if (entry.interval < 1)
		{
			BetterFarming.LOGGER.error("Entry interval is less than 1!");
			return;
		}
		FastRunnableList fastListRunnable = GetOrCreateListRunnable(entry.interval);
		fastListRunnable.Register(entry);
	}

	public void UnregisterRunnable(FastEntry<?> entry)
	{
		FastRunnableList fastListRunnable = GetOrCreateListRunnable(entry.interval);
		fastListRunnable.Unregister(entry);
		if (fastListRunnable.Size() < 1)
			runnableLists.removeIf(listRunnable -> listRunnable.tickInterval == entry.interval);
	}

	public void UnregisterRunnableById(String id)
	{
		var it = runnableLists.listIterator();
		while (it.hasNext())
		{
			var runnable = it.next();
			runnable.UnregisterById(id);
			if (runnable.Size() < 1)
				it.remove();
		}
	}

	public void UnregisterRunnableById(String id, int interval)
	{
		var intervalList = GetRunnableList(interval);
		if (intervalList.isPresent())
		{
			intervalList.get().UnregisterById(id);
			if (runnableLists.size() < 1)
				runnableLists.remove(intervalList.get());
		}
	}

	public <T> void UnregisterRunnableByOwner(T owner)
	{
		var it = runnableLists.listIterator();
		while (it.hasNext())
		{
			var runnable = it.next();
			runnable.UnregisterByOwner(owner);
			if (runnable.Size() < 1)
				it.remove();
		}
	}

	public FastRunnableList GetOrCreateListRunnable(int tickInterval)
	{
		for (int i = 0; i < tickInterval; i++)
		{
			if (runnableLists.get(i).tickInterval == tickInterval)
				return runnableLists.get(i);
		}
		FastRunnableList newRunnable = new FastRunnableList(tickInterval);
		runnableLists.add(newRunnable);
		return newRunnable;
	}

	public Optional<FastRunnableList> GetRunnableList(int tickInterval)
	{
		return runnableLists.stream().filter(runnableLists -> runnableLists.tickInterval == tickInterval).findFirst();
	}

	public void Tick(int currentTick)
	{
		for (FastRunnableList runnable : runnableLists)
		{
			runnable.Tick(currentTick);
		}
	}

	private static class FastRunnableList
	{
		public final int tickInterval;
		private final List<FastEntry<?>> tickEntries;
		private int currentIndex;
		private float listProgress;
		private int nextCycleStart;

		public FastRunnableList(int tickInterval)
		{
			this.tickInterval = tickInterval;
			this.tickEntries = new ArrayList<>();
		}

		public int Size()
		{
			return tickEntries.size();
		}

		public void Tick(int currentTick)
		{
			if (nextCycleStart <= currentTick)
			{
				currentIndex = 0;
				listProgress = 0;
				nextCycleStart = currentTick + tickInterval;
			}

			listProgress += tickEntries.size() / (float) tickInterval;
			int maxIndex = (int) Math.min(tickEntries.size(), listProgress);
			var it = tickEntries.listIterator(currentIndex);
			while (currentIndex < maxIndex)
			{
				currentIndex++;
				var entry = it.next();
				if (entry.IsValid())
				{
					entry.aliveTickCount += tickInterval;
					entry.schedulable.accept(entry);
					if (entry.canceled)
						it.remove();
				}
				else
					it.remove();
			}
		}

		public void Register(FastEntry<?> entry)
		{
			tickEntries.removeIf(listEntry -> listEntry.equals(entry));
			tickEntries.add(entry);
		}

		public void Unregister(FastEntry<?> entry)
		{
			tickEntries.removeIf(listEntry -> listEntry.equals(entry));
		}

		public void UnregisterById(String id)
		{
			tickEntries.removeIf(listEntry -> listEntry.id.equals(id));
		}

		public <T> void UnregisterByOwner(T owner)
		{
			tickEntries.removeIf(listEntry -> listEntry.owner.equals(owner));
		}
	}

	public abstract static class FastEntry<T>
	{

		public String id;
		public T owner;
		public int interval;
		public Schedulable schedulable;

		public boolean canceled;
		public int aliveTickCount;

		public FastEntry()
		{
		}

		public FastEntry(String id, T owner, int interval, Schedulable schedulable)
		{
			this.owner = owner;
			this.interval = interval;
			this.schedulable = schedulable;
			this.id = id;
		}

		public abstract boolean IsValid();

		public NbtCompound SaveInternals(NbtCompound nbt)
		{
			nbt.putString("id", id);
			nbt.putInt("interval", interval);
			nbt.putInt("aliveTickCount", aliveTickCount);
			nbt.putString("scheduleable", FastRunnableScheduler.toString(schedulable));
			nbt.putString("_class", this.getClass().getName());
			ToNbt(nbt);
			return nbt;
		}

		public static FastEntry<?> LoadInternals(NbtCompound nbt)
		{
			try
			{
				Class<?> clazz = Class.forName(nbt.getString("_class"));
				FastEntry<?> entry = (FastEntry<?>) clazz.getConstructor().newInstance();
				entry.id = nbt.getString("id");
				entry.interval = nbt.getInt("interval");
				entry.schedulable = (Schedulable) fromString(nbt.getString("scheduleable"));
				entry.aliveTickCount = nbt.getInt("aliveTickCount");
				entry.FromNbt(nbt);
				return entry;
			}
			catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public abstract NbtCompound ToNbt(NbtCompound nbt);

		public abstract NbtCompound FromNbt(NbtCompound nbt);

		@Override
		public int hashCode()
		{
			return Objects.hash(id, owner, interval);
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			FastEntry<?> fastEntry = (FastEntry<?>) o;

			return interval == fastEntry.interval && id.equals(fastEntry.id) && Objects.equals(owner, fastEntry.owner);
		}
	}

	public static class BlockEntry extends FastEntry<BlockPos>
	{

		public ServerWorld world;

		public BlockEntry()
		{
		}

		public BlockEntry(String id, BlockPos owner, int interval, Schedulable schedulable, ServerWorld world)
		{
			super(id, owner, interval, schedulable);
			this.world = world;
		}

		@Override
		public boolean IsValid()
		{
			return world.isPosLoaded(owner.getX(), owner.getZ());
		}

		@Override
		public NbtCompound ToNbt(NbtCompound nbt)
		{
			return nbt;
		}

		@Override
		public NbtCompound FromNbt(NbtCompound nbt)
		{
			return nbt;
		}
	}

	public static class EntityEntry extends FastEntry<UUID>
	{

		public ServerWorld world;

		public EntityEntry()
		{
		}

		public EntityEntry(String id, UUID owner, int interval, Schedulable schedulable, ServerWorld world)
		{
			super(id, owner, interval, schedulable);
			this.world = world;
		}

		@Override
		public boolean IsValid()
		{
			Entity entity = world.getEntity(owner);
			return entity != null && entity.isAlive() && !entity.isRemoved();
		}

		@Override
		public NbtCompound ToNbt(NbtCompound nbt)
		{
			return nbt;
		}

		@Override
		public NbtCompound FromNbt(NbtCompound nbt)
		{
			return nbt;
		}
	}

	public static class WorldEntry extends FastEntry<ServerWorld>
	{

		public WorldEntry() {}

		public WorldEntry(String id, ServerWorld owner, int interval, Schedulable schedulable)
		{
			super(id, owner, interval, schedulable);
		}

		@Override
		public boolean IsValid()
		{
			return true;
		}

		@Override
		public NbtCompound ToNbt(NbtCompound nbt)
		{
			return nbt;
		}

		@Override
		public NbtCompound FromNbt(NbtCompound nbt)
		{
			return nbt;
		}
	}

	/**
	 * Read the object from Base64 string.
	 */
	public static Object fromString(String s)
	{
		byte[] data = Base64.getDecoder().decode(s);
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			return o;
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Write the object to a Base64 string.
	 */
	public static String toString(Serializable o)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.close();
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
