package me.bscal.betterfarming.common.seasons;

import me.bscal.betterfarming.BetterFarming;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.world.PersistentState;

public class SeasonManager extends PersistentState implements ServerTickEvents.StartTick
{

	public static final int SPRING = 0;
	public static final int SUMMER = 1;
	public static final int AUTUMN = 2;
	public static final int WINTER = 3;

	public int currentSeason;
	public long ticksSinceCreation;

	private ServerWorld m_world;

	public SeasonManager()
	{
		ServerTickEvents.START_SERVER_TICK.register(this);
	}

	public static SeasonManager GetOrCreate(ServerWorld world)
	{
		SeasonManager seasons = world.getPersistentStateManager()
				.getOrCreate(SeasonManager::LoadFromNbt, SeasonManager::new,
						BetterFarming.MOD_ID + "_seasons");
		seasons.SetWorld(world);
		return seasons;
	}

	private static SeasonManager LoadFromNbt(NbtCompound tag)
	{
		SeasonManager seasons = new SeasonManager();
		tag.putInt("season", seasons.currentSeason);
		tag.putLong("ticks", seasons.ticksSinceCreation);
		return seasons;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt)
	{
		currentSeason = nbt.getInt("season");
		ticksSinceCreation = nbt.getLong("ticks");
		return nbt;
	}

	@Override
	public void onStartTick(MinecraftServer server)
	{
		ticksSinceCreation++;
	}

	public long GetTicks()
	{
		return ticksSinceCreation;
	}

	public void PassTime()
	{
		ticksSinceCreation += MinecraftDate.TICKS_PER_DAY - (m_world.getTime() % MinecraftDate.TICKS_PER_DAY);
	}

	protected void SetWorld(ServerWorld world)
	{
		this.m_world = world;
	}

	public static class MinecraftDate
	{
		public static final long TICKS_PER_DAY = 24000;
		public static final long TICKS_PER_MONTH = 30 * TICKS_PER_DAY;
		public static final long TICKS_PER_YEAR = 12 * TICKS_PER_MONTH;
		public static final long TICKS_PER_AGE = 1000 * TICKS_PER_YEAR;

		public long day;
		public long month;
		public long year;
		public long age;
		public long remainingTicks;
		public long serverWorldTimeOfDay;

		public MinecraftDate() {}

		public MinecraftDate(long ticks)
		{
			ParseDateFromTickTime(ticks);
		}

		public MinecraftDate(long ticks, ServerWorld world)
		{
			ParseDateFromTickTime(ticks);
			serverWorldTimeOfDay = world.getTimeOfDay();
		}

		public void ParseDateFromTickTime(long ticks)
		{
			age = Math.floorDiv(ticks, TICKS_PER_AGE);
			ticks -= age * TICKS_PER_AGE;

			year = Math.floorDiv(ticks , TICKS_PER_YEAR);
			ticks -= year * TICKS_PER_YEAR;

			month = Math.floorDiv(ticks , TICKS_PER_MONTH);
			ticks -= month * TICKS_PER_MONTH;

			day = Math.floorDiv(ticks , TICKS_PER_DAY);
			ticks -= day * TICKS_PER_DAY;

			remainingTicks = ticks;
		}

		/**
		 * Parses ticks into minecraft time. 6:00 is 0 ticks. Uses 24-hour clock.
		 * @return Int array of size 2 with the hours and minutes. Index 0 = hours. Index 1 = minutes.
		 */
		public static int[] ParseHourAndMinutesFromTicks(int ticks)
		{
			int[] hoursAndMinutes = new int[2];
			ticks = Math.min(ticks, 24000);

			int hours = Math.floorDiv(ticks, 1000);
			int hoursTime = 6 + hours; // Minecraft time starts at 6:00
			hoursAndMinutes[0] = (hoursTime > 23) ? hoursTime - 24 : hoursTime;
			ticks -= hours * 1000;

			hoursAndMinutes[1] = (int)Math.floor((float) ticks / 16.6f);

			return hoursAndMinutes;
		}

	}

}
