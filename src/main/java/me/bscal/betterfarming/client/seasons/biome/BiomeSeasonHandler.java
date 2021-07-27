package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.common.seasons.SeasonClock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT) public class BiomeSeasonHandler
{
	public final Map<Biome, BiomeChanger> biomeEffectChangerMap = new HashMap<>();
	public final SeasonClock seasonClock = new SeasonClock();
	public boolean recievedSyncPacket;
	public boolean haveBiomeChangersLoaded;

	private ClientWorld m_world;

	public BiomeSeasonHandler()
	{
	}

	public void RegisterBiomeChangers(ClientWorld world)
	{
		haveBiomeChangersLoaded = true;
		world.getRegistryManager().get(Registry.BIOME_KEY).getEntries().forEach((key) -> {
			Register(new BiomeChangers.SimpleBiomeChanger(key.getKey(), world));
		});

	}

	public void Reload(ClientWorld world)
	{
		biomeEffectChangerMap.clear();
		RegisterBiomeChangers(world);
		MinecraftClient.getInstance().worldRenderer.reload();
	}

	public void Register(BiomeChanger changer)
	{
		biomeEffectChangerMap.put(changer.biome, changer);
	}

	public void UpdateSeasonColors()
	{
		for (BiomeChanger changer : biomeEffectChangerMap.values())
		{
			//BetterFarming.LOGGER.info(changer.key.toString() + " Has updated.");
			//changer.GetColor(seasonClock.currentSeason);
		}
	}

	private void SyncTime(int season, int ticksInCurrentSeason, long ticks)
	{
		recievedSyncPacket = true;
		boolean seasonChanged = seasonClock.currentSeason != season;
		seasonClock.currentSeason = season;
		seasonClock.ticksInCurrentSeason = ticksInCurrentSeason;
		seasonClock.ticksSinceCreation = ticks;
		if (seasonChanged)
		{
			//UpdateSeasonColors();
			MinecraftClient.getInstance().worldRenderer.reload();
		}
	}

	public static ClientPlayNetworking.PlayChannelHandler SyncTimeS2CPacketHandler()
	{
		return (client, handler, buf, responseSender) -> {
			int season = buf.readInt();
			int ticksInCurrentSeason = buf.readInt();
			long ticks = buf.readLong();

			client.execute(() -> {
				if (BetterFarmingClient.GetBiomeSeasonHandler() != null)
					BetterFarmingClient.GetBiomeSeasonHandler().SyncTime(season, ticksInCurrentSeason, ticks);
			});
		};
	}
}
