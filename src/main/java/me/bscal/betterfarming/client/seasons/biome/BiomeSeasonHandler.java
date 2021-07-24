package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.common.seasons.SeasonClock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT) public class BiomeSeasonHandler
{
	public final Map<Biome, BiomeChanger> biomeEffectChangerMap = new HashMap<>();
	public final SeasonClock seasonClock = new SeasonClock();
	public boolean recievedSyncPacket;
	public boolean haveBiomeChangersLoaded;

	private ClientWorld m_world;

	public BiomeSeasonHandler() { }

	public void RegisterBiomeChangers(ClientWorld world)
	{
		haveBiomeChangersLoaded = true;
		Register(new BiomeChangers.SimpleBiomeChanger(BiomeKeys.PLAINS, world));
	}

	public void Register(BiomeChanger changer)
	{
		biomeEffectChangerMap.put(changer.biome, changer);
	}

	public void UpdateSeasonColors()
	{
		for (BiomeChanger changer : biomeEffectChangerMap.values())
		{
			BetterFarming.LOGGER.info(changer.key.toString() + " Has updated.");
			changer.GetColor(seasonClock.currentSeason);
		}
	}

	private void SyncTime(long ticks, int season)
	{
		recievedSyncPacket = true;
		seasonClock.ticksSinceCreation = ticks;
		seasonClock.currentSeason = season;

		if (seasonClock.ticksSinceCreation % 200 == 0)
		{
			UpdateSeasonColors();
			MinecraftClient.getInstance().worldRenderer.reload();
		}
	}

	public static ClientPlayNetworking.PlayChannelHandler SyncTimeS2CPacketHandler()
	{
		return (client, handler, buf, responseSender) -> {
			long ticks = buf.readLong();
			int season = buf.readInt();

			client.execute(() -> {
				if (BetterFarmingClient.GetBiomeSeasonHandler() != null)
					BetterFarmingClient.GetBiomeSeasonHandler().SyncTime(ticks, season);
			});
		};
	}
}
