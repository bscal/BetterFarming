package me.bscal.betterfarming.common.utils.schedulers;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.dimension.DimensionType;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Inspiration from HugsLib's tick scheduler a Rimworld modding library. Thought it could be a useful addition when I want to
 * scheduler runnables for mods or plugins more efficiently
 */
public final class FastRunnableScheduler
{

	public static final FastRunnableScheduler INSTANCE = new FastRunnableScheduler();

	private final List<FastRunnableList> runnableLists;

	public FastRunnableScheduler()
	{
		runnableLists = new ArrayList<>();
	}

	public void RegisterRunnable(int interval, Schedulable schedulable)
	{
		var entry = new FastEntry("", interval);
		entry.schedulable = schedulable;
		RegisterRunnable(entry);
	}

	public void RegisterRunnable(FastEntry entry)
	{
		if (entry == null) return;
		if (entry.schedulable == null)
		{
			BetterFarming.LOGGER.error("Scheduleable is null");
			return;
		}
		if (entry.owner != null && !entry.owner.IsValid())
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

	public void UnregisterRunnable(FastEntry entry)
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
		GetRunnableList(interval).ifPresent(intervalList ->
		{
			intervalList.UnregisterById(id);
			if (intervalList.Size() < 1)
				runnableLists.remove(intervalList);
		});
	}

	public void UnregisterRunnableByOwner(SchedulableOwner owner)
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
		for (FastRunnableList runnableList : runnableLists)
		{
			if (runnableList.tickInterval == tickInterval)
				return runnableList;
		}
		FastRunnableList newRunnable = new FastRunnableList(tickInterval);
		runnableLists.add(newRunnable);
		return newRunnable;
	}

	public Optional<FastRunnableList> GetRunnableList(int tickInterval)
	{
		return runnableLists.stream().filter(runnableLists -> runnableLists.tickInterval == tickInterval).findFirst();
	}

	public void Save(MinecraftServer server)
	{
		File scheduleSavePath  = new File(DimensionType.getSaveDirectory(server.getOverworld().getRegistryKey(), server
				.getSavePath(WorldSavePath.ROOT)
				.toFile()) + "/schedulables");
		scheduleSavePath.mkdirs();
		NbtCompound root = new NbtCompound();
		NbtList rootList = new NbtList();
		runnableLists.forEach(list -> {
			list.tickEntries.forEach(entry -> {
				if (entry.schedulable instanceof PersistentSchedulable)
				{
					rootList.add(entry.Serialize(new NbtCompound()));
				}
			});
		});
		root.put("entries", rootList);
		try
		{
			NbtIo.writeCompressed(root, new File(scheduleSavePath, "schedulables.dat"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void Load(MinecraftServer server)
	{
		File scheduleSavePath  = new File(DimensionType.getSaveDirectory(server.getOverworld().getRegistryKey(), server
				.getSavePath(WorldSavePath.ROOT)
				.toFile()) + "/schedulables");
		if (scheduleSavePath.exists())
		{
			try
			{
				NbtCompound root = NbtIo.readCompressed(new File(scheduleSavePath, "schedulables.dat"));
				NbtList rootList = root.getList("entries", NbtElement.COMPOUND_TYPE);
				for (NbtElement ele : rootList)
				{
					RegisterRunnable(FastEntry.FromNbt((NbtCompound) ele));
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
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
		private final List<FastEntry> tickEntries;
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
			int maxIndex = Math.min(tickEntries.size(), (int)Math.ceil(listProgress));
			var it = tickEntries.listIterator(currentIndex);
			while (currentIndex < maxIndex)
			{
				currentIndex++;
				var entry = it.next();
				if (entry.owner.IsValid())
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

		public void Register(FastEntry entry)
		{
			tickEntries.removeIf(listEntry -> listEntry.equals(entry));
			tickEntries.add(entry);
		}

		public void Unregister(FastEntry entry)
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

		public Optional<FastEntry> Get(String id)
		{
			return tickEntries.stream().filter(e -> e.id.equals(id)).findFirst();
		}

		public FastEntry[] GetAll(String id)
		{
			return (FastEntry[]) tickEntries.stream().filter(e -> e.id.equals(id)).toArray();
		}
	}

	public static class FastEntry
	{

		public String id;
		public int interval;
		public Schedulable schedulable;
		public SchedulableOwner owner;
		public boolean canceled;
		public int aliveTickCount;

		public FastEntry()
		{
		}

		public FastEntry(String id, int interval)
		{
			this.id = id;
			this.interval = interval;
		}

		public NbtCompound Serialize(NbtCompound nbt)
		{
			if (schedulable instanceof PersistentSchedulable persistentSchedulable)
			{
				nbt.putString("id", id);
				nbt.putInt("interval", interval);
				nbt.putInt("aliveTickCount", aliveTickCount);
				persistentSchedulable.Serialize(nbt);
				nbt.putString("owner", owner.getClass().getName());
				owner.Serialize(nbt);
				return nbt;
			}

			return nbt;
		}

		public static FastEntry FromNbt(NbtCompound nbt)
		{
			try
			{
				FastEntry entry = new FastEntry();
				entry.schedulable = PersistentSchedulable.Deserialize(nbt);
				if (entry.schedulable == null)
					return null;
				entry.owner = (SchedulableOwner) Class.forName(nbt.getString("owner")).getConstructor().newInstance();
				entry.owner.Deserialize(nbt);
				entry.id = nbt.getString("id");
				entry.interval = nbt.getInt("interval");
				entry.aliveTickCount = nbt.getInt("aliveTickCount");
				return entry;
			}
			catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(id, interval, owner);
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			FastEntry entry = (FastEntry) o;

			if (interval != entry.interval)
				return false;
			if (!id.equals(entry.id))
				return false;
			return Objects.equals(owner, entry.owner);
		}
	}
}
