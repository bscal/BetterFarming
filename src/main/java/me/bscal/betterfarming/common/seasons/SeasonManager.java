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

public class SeasonManager extends PersistentState
{
	private final SeasonClock m_seasonClock = new SeasonClock();
	private ServerWorld m_world;
	private int m_numOfDaysPerSeason = 30;
	private int m_maxSeasons = 4;
	private long m_lastTimeChecked;

	private final PacketByteBuf m_bufCache;

	public SeasonManager()
	{
		m_bufCache = new PacketByteBuf(Unpooled.buffer(12));
	}

	public static SeasonManager GetOrCreate(ServerWorld world)
	{
		SeasonManager seasons = world.getPersistentStateManager()
				.getOrCreate(SeasonManager::LoadFromNbt, SeasonManager::new,
						BetterFarming.MOD_ID + "_seasons");
		//BetterFarming.Log("[GetOrCreate] " + world.getRegistryKey().toString());
		seasons.SetWorld(world);
		return seasons;
	}

	private static SeasonManager LoadFromNbt(NbtCompound nbt)
	{
		SeasonManager seasons = new SeasonManager();
		seasons.m_seasonClock.currentSeason = nbt.getInt("season");
		seasons.m_seasonClock.ticksSinceCreation = nbt.getLong("ticks");
		return seasons;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt)
	{
		nbt.putInt("season", m_seasonClock.currentSeason);
		nbt.putLong("ticks", m_seasonClock.ticksSinceCreation);
		return nbt;
	}

	/**
	 * Updates the season, season time, and will trigger new day event. Does not work if you try to go back in time.
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
			m_seasonClock.ticksSinceCreation += timeOfDay - (newDay ?
					m_lastTimeChecked - 24000 :
					m_lastTimeChecked);

			// Sync season packet ever 10 ticks instead of every tick.1
			if (m_seasonClock.ticksSinceCreation % 10 == 0)
			{
				m_bufCache.clear();
				m_bufCache.writeLong(m_seasonClock.ticksSinceCreation);
				m_bufCache.writeInt(m_seasonClock.currentSeason);
				for (ServerPlayerEntity player : PlayerLookup.all(BetterFarming.GetServer().get()))
					ServerPlayNetworking.send(player, BetterFarming.SYNC_PACKET, m_bufCache);

				if (BetterFarming.DEBUG)
					BetterFarming.Log(String.format(
							"[Time Ticked] ticks: %d : season: %d. --- timeday(%d)/lastday(%d) | diff(%d)",
							m_seasonClock.ticksSinceCreation, m_seasonClock.currentSeason, timeOfDay,
							m_lastTimeChecked,
							timeOfDay - (newDay ? m_lastTimeChecked - 24000 : m_lastTimeChecked)));
			}

			long days = GetDays(m_seasonClock.ticksSinceCreation);
			if (newDay)
				SeasonEvents.NEW_DAY.invoker().OnNewDay(m_seasonClock.ticksSinceCreation, days);

			//int newSeason = (int) (Math.floorDiv(days, m_numOfDaysPerSeason) % m_maxSeasons);
			if (false)
			{
				SeasonEvents.SEASON_CHANGED.invoker()
						.OnSeasonChanged(m_seasonClock.currentSeason,
								SeasonManager.GetDays(m_seasonClock.ticksSinceCreation));
			}
			m_lastTimeChecked = timeOfDay;
		}

	}

	public static long GetDays(long ticksSinceCreation)
	{
		return Math.floorDiv(ticksSinceCreation, 24000);
	}

	public void SetSeason(int season)
	{
		this.m_seasonClock.currentSeason = Math.min(m_maxSeasons - 1, Math.max(0, season));
	}

	protected void SetWorld(ServerWorld world)
	{
		this.m_world = world;
	}
}
