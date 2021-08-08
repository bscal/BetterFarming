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
	private final SeasonClock m_seasonClock;
	private long m_lastTimeChecked;
	private int m_counter;

	public SeasonManager()
	{
		// TODO loading from configs
		m_seasonClock = BetterFarming.SEASON_CLOCK;
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
		return world.getPersistentStateManager()
				.getOrCreate(SeasonManager::LoadFromNbt, SeasonManager::new, BetterFarming.MOD_ID + "_seasons");
	}

	private static SeasonManager LoadFromNbt(NbtCompound nbt)
	{
		SeasonManager seasons = new SeasonManager();
		seasons.m_seasonClock.currentSeason = Math.min(Seasons.MAX_SEASONS - 1, Math.max(0, nbt.getInt("season")));
		seasons.m_seasonClock.ticksInCurrentSeason = Math.min(SeasonSettings.Root.ticksPerSeason.getValue() - 1,
				Math.max(0, nbt.getInt("ticksInCurrentSeason")));
		seasons.m_seasonClock.ticksSinceCreation = nbt.getLong("ticks");
		return seasons;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt)
	{
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

			if (m_seasonClock.ticksInCurrentSeason > SeasonSettings.Root.ticksPerSeason.getValue())
			{
				ProgessSeason();
				SeasonEvents.SEASON_CHANGED.invoker()
						.OnSeasonChanged(m_seasonClock.currentSeason, SeasonManager.GetDays(m_seasonClock.ticksSinceCreation));
			}

			if (newDay)
				SeasonEvents.NEW_DAY.invoker().OnNewDay(m_seasonClock.ticksSinceCreation, GetDays(m_seasonClock.ticksSinceCreation));

			// So we dont send a packet ever tick
			if (m_counter++ > 20)
			{
				m_counter = 0;
				SyncSeasonTimeS2C();

				if (BetterFarming.DEBUG)
					BetterFarming.Log(String.format("[Time Ticked] ticks: %d : season: %d. --- timeday(%d)/lastday(%d) | diff(%d)",
							m_seasonClock.ticksSinceCreation, m_seasonClock.currentSeason, timeOfDay, m_lastTimeChecked,
							timeOfDay - (newDay ? m_lastTimeChecked - 24000 : m_lastTimeChecked)));
			}
			m_lastTimeChecked = timeOfDay;
			markDirty();
		}
	}

	protected void SyncSeasonTimeS2C()
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer(1 + 4 + 8, 1 + 4 + 8));
		buf.writeByte(m_seasonClock.currentSeason);
		buf.writeInt(m_seasonClock.ticksInCurrentSeason);
		buf.writeLong(m_seasonClock.ticksSinceCreation);
		for (ServerPlayerEntity player : PlayerLookup.all(BetterFarming.GetServer()))
			ServerPlayNetworking.send(player, BetterFarming.SYNC_PACKET, buf);

	}

	public static long GetDays(long ticksSinceCreation)
	{
		return Math.floorDiv(ticksSinceCreation, 24000);
	}

	public void ProgessSeason()
	{
		int nextSeason = m_seasonClock.currentSeason + 1;
		SetSeason((nextSeason > Seasons.MAX_SEASONS) ? 0 : nextSeason);
	}

	public void SetSeason(int season)
	{
		season = Math.min(Seasons.MAX_SEASONS - 1, Math.max(0, season));
		m_seasonClock.ticksInCurrentSeason = 0;
		if (season != m_seasonClock.currentSeason)
		{
			m_seasonClock.currentSeason = season;
			SyncSeasonTimeS2C();
		}
		SeasonSettings.Root.Generation.seasonSeed.setValue(BetterFarming.RAND.nextInt(Integer.MAX_VALUE));
		markDirty();
	}

	public SeasonClock GetSeasonClock()
	{
		return m_seasonClock;
	}
}
