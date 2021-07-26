package me.bscal.betterfarming.common.seasons;

import io.netty.buffer.Unpooled;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.events.SeasonEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class SeasonManager extends PersistentState
{
	private final SeasonClock m_seasonClock = new SeasonClock();
	private ServerWorld m_world;
	private int m_numOfTicksPerSeason = 30 * 24000;
	private int m_maxSeasons = 4;
	private long m_lastTimeChecked;

	private final PacketByteBuf m_bufCache;

	public SeasonManager()
	{
		// TODO loading from configs
		m_bufCache = new PacketByteBuf(Unpooled.buffer(4 + 4 + 8));
	}

	public static SeasonManager GetOrCreate()
	{
		ServerWorld world = BetterFarming.GetServer().getWorld(World.OVERWORLD);
		if (world == null)
			BetterFarming.LOGGER.error("ServerWorld for OVERWORLD is null! Maybe GetServer() is null?");
		return GetOrCreate(world);
	}

	public static SeasonManager GetOrCreate(ServerWorld world)
	{
		SeasonManager seasons = world.getPersistentStateManager()
				.getOrCreate(SeasonManager::LoadFromNbt, SeasonManager::new,
						BetterFarming.MOD_ID + "_seasons");
		seasons.m_world = world;
		return seasons;
	}

	private static SeasonManager LoadFromNbt(NbtCompound nbt)
	{
		SeasonManager seasons = new SeasonManager();
		seasons.m_seasonClock.currentSeason = Math.min(seasons.m_maxSeasons - 1,
				Math.max(0, nbt.getInt("season")));
		seasons.m_seasonClock.ticksInCurrentSeason = Math.min(seasons.m_numOfTicksPerSeason - 1,
				Math.max(0, nbt.getInt("ticksInCurrentSeason")));
		seasons.m_seasonClock.ticksSinceCreation = nbt.getLong("ticks");
		BetterFarming.LOGGER.info("read = " + seasons.m_seasonClock.ticksSinceCreation);
		return seasons;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt)
	{
		BetterFarming.LOGGER.info("write = " + m_seasonClock.ticksSinceCreation);
		nbt.putInt("season", m_seasonClock.currentSeason);
		nbt.putInt("ticksInCurrentSeason", m_seasonClock.ticksInCurrentSeason);
		nbt.putLong("ticks", m_seasonClock.ticksSinceCreation);
		return nbt;
	}

	/**
	 * Updates the season, season time, and will trigger new day event. Does not work if you try to go back in time.
	 * Called from `World.tick`, time set command, and sleeping
	 */
	public void Update(long timeOfDay)
	{
		// Not sure if needed but I had to make sure we are not update clock twice in certain situations.
		if (m_lastTimeChecked != timeOfDay)
		{
			boolean newDay = timeOfDay < m_lastTimeChecked;
			// Gets the difference of the time of day to the new time of day.
			// Not sure if this is the best method but this is called on every tick, set time command, player sleeping.
			// So I didnt want to calculate 2 ticks and since max time of day is 24000, this was the best way I could come up with.
			// 23001 (timeOfDay) - 23000 (m_lastTimeChecked) = 1
			// 4000 (timeOfDay) - (23000 (m_lastTimeChecked) - 24000) = 5000 -> also a new day
			long ticksPassed = timeOfDay - (newDay ? m_lastTimeChecked - 24000 : m_lastTimeChecked);
			m_seasonClock.ticksSinceCreation += ticksPassed;
			m_seasonClock.ticksInCurrentSeason += ticksPassed;

			long days = GetDays(m_seasonClock.ticksSinceCreation);
			if (newDay)
				SeasonEvents.NEW_DAY.invoker().OnNewDay(m_seasonClock.ticksSinceCreation, days);

			if (this.m_seasonClock.ticksInCurrentSeason > m_numOfTicksPerSeason)
			{
				ProgessSeason();
				SeasonEvents.SEASON_CHANGED.invoker()
						.OnSeasonChanged(m_seasonClock.currentSeason,
								SeasonManager.GetDays(m_seasonClock.ticksSinceCreation));
			}

			// So we dont send a packet ever tick
			if (m_seasonClock.ticksSinceCreation % 20 == 0)
			{
				SyncSeasonTimeS2C();

				if (BetterFarming.DEBUG)
					BetterFarming.Log(String.format(
							"[Time Ticked] ticks: %d : season: %d. --- timeday(%d)/lastday(%d) | diff(%d)",
							m_seasonClock.ticksSinceCreation, m_seasonClock.currentSeason, timeOfDay,
							m_lastTimeChecked,
							timeOfDay - (newDay ? m_lastTimeChecked - 24000 : m_lastTimeChecked)));
			}
			m_lastTimeChecked = timeOfDay;
			markDirty();
		}
	}

	protected void SyncSeasonTimeS2C()
	{
		m_bufCache.clear();
		m_bufCache.writeInt(m_seasonClock.currentSeason);
		m_bufCache.writeInt(m_seasonClock.ticksInCurrentSeason);
		m_bufCache.writeLong(m_seasonClock.ticksSinceCreation);
		for (ServerPlayerEntity player : PlayerLookup.all(BetterFarming.GetServer()))
			ServerPlayNetworking.send(player, BetterFarming.SYNC_PACKET, m_bufCache);

	}

	public static long GetDays(long ticksSinceCreation)
	{
		return Math.floorDiv(ticksSinceCreation, 24000);
	}

	public void ProgessSeason()
	{
		int nextSeason = m_seasonClock.currentSeason + 1;
		SetSeason((nextSeason > m_maxSeasons) ? 0 : nextSeason);
	}

	public void SetSeason(int season)
	{
		season = Math.min(m_maxSeasons - 1, Math.max(0, season));
		m_seasonClock.ticksInCurrentSeason = 0;
		if (season != m_seasonClock.currentSeason)
		{
			m_seasonClock.currentSeason = season;
			SyncSeasonTimeS2C();
		}
		markDirty();
	}
}
