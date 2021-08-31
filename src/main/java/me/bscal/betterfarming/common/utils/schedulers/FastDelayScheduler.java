package me.bscal.betterfarming.common.utils.schedulers;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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

	public void Save(MinecraftServer server)
	{
		File scheduleSavePath  = new File(DimensionType.getSaveDirectory(server.getOverworld().getRegistryKey(), server
				.getSavePath(WorldSavePath.ROOT)
				.toFile()) + "/schedulables");
		scheduleSavePath.mkdirs();
		NbtCompound root = new NbtCompound();
		NbtList rootList = new NbtList();
		entries.forEach(entry -> {
			if (entry.persistent)
			{
				rootList.add(entry.Serialize(new NbtCompound()));
			}
		});
		root.put("entries", rootList);
		try
		{
			NbtIo.writeCompressed(root, new File(scheduleSavePath, "delayables.dat"));
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
				NbtCompound root = NbtIo.readCompressed(new File(scheduleSavePath, "delayables.dat"));
				NbtList rootList = root.getList("entries", NbtElement.COMPOUND_TYPE);
				for (NbtElement ele : rootList)
				{
					ScheduleEntry(DelayEntry.FromNbt((NbtCompound) ele));
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
		lastProcessedTick = currentTick;
		while (entries.peek() != null)
		{
			var entry = entries.remove();
			if (entry.dueAtTick > currentTick)
				return;
			boolean shouldReschedule = DoRunnable(entry);
			if (entry.repeat && shouldReschedule)
			{
				ScheduleEntry(new DelayEntry(entry.interval, currentTick + entry.interval, true, entry.persistent, entry.delayable));
			}
		}
	}

	public void ScheduleRunnable(int dueInTicks, boolean repeat, boolean persistent, Delayable delayable)
	{
		if (lastProcessedTick < 0)
			return;
		if (delayable == null)
			return;
		if (dueInTicks < 0)
			return;
		if (dueInTicks == 0 && repeat)
			return;
		DelayEntry entry = new DelayEntry(dueInTicks, lastProcessedTick + dueInTicks, repeat, persistent, delayable);
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
		return entry.delayable.test(entry);
	}

	public static record DelayEntry(int interval, int dueAtTick, boolean repeat, boolean persistent, Delayable delayable)
			implements Serializable
	{

		public NbtCompound Serialize(NbtCompound nbt)
		{
			nbt.putInt("interval", interval);
			nbt.putInt("dueAtTick", dueAtTick - BetterFarming.GetServer().getTicks());
			nbt.putBoolean("repeat", repeat);
			nbt.putBoolean("persistent", persistent);
			nbt.putString("delayable", Utils.ToString(delayable));
			return nbt;
		}

		public static DelayEntry FromNbt(NbtCompound nbt)
		{
			return new DelayEntry(nbt.getInt("interval"), nbt.getInt("dueAtTick"), nbt.getBoolean("repeat"), nbt.getBoolean("persistent"),
					(Delayable) Utils.FromString(nbt.getString("delayable")));
		}

	}

}
