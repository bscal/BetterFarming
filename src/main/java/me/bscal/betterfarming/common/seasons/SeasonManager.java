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
	private boolean m_isSkippingTime;

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

	public void Tick()
	{
		ticksSinceCreation++;
		if (!m_isSkippingTime && ticksSinceCreation % 24000 == 0)
		{
			long days = GetDays();
			SeasonEvents.NEW_DAY.invoker().NewDay(ticksSinceCreation, days, false);
			if (days >= m_numOfDaysPerSeason)
				currentSeason = (currentSeason > m_maxSeasons) ? 1 : currentSeason + 1;
		}
		m_isSkippingTime = false;
	}

	public void PassTime()
	{
		m_isSkippingTime = true;
		ticksSinceCreation += MinecraftDate.TICKS_PER_DAY - (m_world.getTime() % MinecraftDate.TICKS_PER_DAY);
		SeasonEvents.NEW_DAY.invoker().NewDay(ticksSinceCreation, GetDays(), true);
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
