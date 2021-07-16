package me.bscal.betterfarming.common.seasons;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.events.SeasonEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

public class SeasonManager extends PersistentState
{

	public static final int ALL = 0;
	public static final int SPRING = 1;
	public static final int SUMMER = 2;
	public static final int AUTUMN = 3;
	public static final int WINTER = 4;

	public int currentSeason;
	public long ticksSinceCreation;

	private ServerWorld m_world;
	private int m_numOfDaysPerSeason;
	private int m_maxSeasons = 4;
	private long m_lastTimeChecked;

	public SeasonManager()
	{

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

	/**
	 *	Updates the season, season time, and will trigger new day event. Does not work if you try to go back in time.
	 */
	public void Update(long timeOfDay)
	{
		// TODO actually i might not need this
		if (m_lastTimeChecked != timeOfDay)
		{
			// Gets the difference of the time of day to the new time of day.
			// Not sure if this is the best method but this is called on every tick, set time command, player sleeping.
			// So I didnt want to calculate 2 ticks and since max time of day is 24000, this was the best way I could come up with.
			// 23001 (timeOfDay) - 23000 (m_lastTimeChecked) = 1
			// 4000 (timeOfDay) - (23000 (m_lastTimeChecked) - 24000) = 5000 -> also a new day
			boolean newDay = timeOfDay < m_lastTimeChecked;
			ticksSinceCreation += timeOfDay - (newDay ?  m_lastTimeChecked : m_lastTimeChecked - 24000);
			if (newDay)
			{
				long days = GetDays();
				SeasonEvents.NEW_DAY.invoker().OnNewDay(ticksSinceCreation, days);
				if (days >= m_numOfDaysPerSeason)
				{
					currentSeason = (currentSeason > m_maxSeasons) ? 1 : currentSeason + 1;
					SeasonEvents.SEASON_CHANGED.invoker().OnSeasonChanged(currentSeason, days);
				}

			}
		}
		m_lastTimeChecked = timeOfDay;
	}

	public long GetDays()
	{
		return Math.floorDiv(ticksSinceCreation, 24000);
	}

	protected void SetWorld(ServerWorld world)
	{
		this.m_world = world;
	}
}
