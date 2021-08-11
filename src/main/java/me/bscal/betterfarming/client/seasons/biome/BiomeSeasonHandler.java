package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.common.seasons.SeasonClock;
import me.bscal.betterfarming.common.utils.RegistryObjToObjMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT) public class BiomeSeasonHandler
{
	private final RegistryObjToObjMap<Biome, BiomeChanger> m_changerMap;
	public final SeasonClock seasonClock = new SeasonClock();
	public boolean receivedSyncPacket;

	/**
	 * This is loaded when the world is loaded to access the dynamic registry for biomes.
	 */
	public BiomeSeasonHandler(ClientWorld world)
	{
		m_changerMap = new RegistryObjToObjMap<>(world.getRegistryManager().get(Registry.BIOME_KEY));
		InitChangers(world);
	}

	private void InitChangers(ClientWorld world)
	{
		world.getRegistryManager()
				.get(Registry.BIOME_KEY)
				.getEntries()
				.forEach((key) -> Register(new BiomeChangers.SimpleBiomeChanger(key.getKey()), key.getValue()));
	}

	public void Register(BiomeChanger changer, Biome biome)
	{
		m_changerMap.put(biome, changer);
		changer.InitChanger(biome);
	}

	public RegistryObjToObjMap<Biome, BiomeChanger> GetChangers()
	{
		return m_changerMap;
	}

	public void Reload(ClientWorld world)
	{
		Clean();
		InitChangers(world);
		MinecraftClient.getInstance().worldRenderer.reload();
	}

	private void Clean()
	{
		m_changerMap.clear();
	}

	private void SyncTime(int season, int ticksInCurrentSeason, long ticks)
	{
		receivedSyncPacket = true;
		boolean seasonChanged = seasonClock.currentSeason != season;
		seasonClock.currentSeason = season;
		seasonClock.ticksInCurrentSeason = ticksInCurrentSeason;
		seasonClock.ticksSinceCreation = ticks;
		if (seasonChanged)
		{
			MinecraftClient.getInstance().worldRenderer.reload();
		}
	}

	public static ClientPlayNetworking.PlayChannelHandler SyncTimeS2CPacketHandler()
	{
		return (client, handler, buf, responseSender) -> {
			int season = buf.readByte();
			int ticksInCurrentSeason = buf.readInt();
			long ticks = buf.readLong();

			client.execute(() -> {
				if (BetterFarmingClient.GetBiomeSeasonHandler() != null)
					BetterFarmingClient.GetBiomeSeasonHandler().SyncTime(season, ticksInCurrentSeason, ticks);
			});
		};
	}
}
