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
	public RegistryObjToObjMap<Biome, BiomeChanger> changerMap;
	public final Map<RegistryKey<Biome>, BiomeChanger> biomeEffectChangerMap = new HashMap<>();
	public final SeasonClock seasonClock = new SeasonClock();
	public boolean receivedSyncPacket;
	public boolean haveBiomeChangersLoaded;

	/**
	 * This is loaded when the world is loaded to access the dynamic registry for biomes.
	 */
	public void RegisterBiomeChangers(ClientWorld world)
	{
		changerMap = new RegistryObjToObjMap<>(world.getRegistryManager().get(Registry.BIOME_KEY));
		haveBiomeChangersLoaded = true;
		world.getRegistryManager()
				.get(Registry.BIOME_KEY)
				.getEntries()
				.forEach((key) -> Register(new BiomeChangers.SimpleBiomeChanger(key.getKey()), key.getValue()));
	}

	public void Register(BiomeChanger changer, Biome biome)
	{
		biomeEffectChangerMap.put(changer.key, changer);
		changer.InitChanger(biome);
	}

	public void Reload(ClientWorld world)
	{
		Clean();
		RegisterBiomeChangers(world);
		MinecraftClient.getInstance().worldRenderer.reload();
	}

	private void Clean()
	{
		biomeEffectChangerMap.clear();
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
