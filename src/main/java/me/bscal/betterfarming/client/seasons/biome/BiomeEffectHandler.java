package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.seasons.SeasonClock;
import me.bscal.betterfarming.common.utils.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.mixin.biome.modification.BiomeEffectsAccessor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;

import java.util.*;

@Environment(EnvType.CLIENT) public class BiomeEffectHandler
{

	public final Map<Biome, BiomeEffectChanger> m_biomeEffectChangerMap = new HashMap<>();
	public boolean reloadWorld = false;
	private final SeasonClock m_seasonClock = new SeasonClock();
	private final List<BiomeEffectChanger> m_biomeEffectChangers = new ArrayList<>();
	private final ClientWorld m_world;

	public BiomeEffectHandler(ClientWorld world)
	{
		m_world = world;
		Register(BiomeChangers.PLAINS_CHANGER);
	}

	public void Register(BiomeEffectChanger changer)
	{
		changer.biome = m_world.getRegistryManager().get(Registry.BIOME_KEY).get(changer.key);
		m_biomeEffectChangerMap.put(changer.biome, changer);
		m_biomeEffectChangers.add(changer);
	}

	public ClientPlayNetworking.PlayChannelHandler SyncTime()
	{
		return (client, handler, buf, responseSender) -> {
			if (client.world.isClient)
			{
				long ticks = buf.readLong();
				int season = buf.readInt();
				m_seasonClock.ticksSinceCreation = ticks;
				m_seasonClock.currentSeason = season;
				if (m_seasonClock.ticksSinceCreation % 100 == 0)
				{
					reloadWorld = true;
				}
			}
		};
	}

	public void UpdateSeasonColors()
	{
		for (BiomeEffectChanger changer : m_biomeEffectChangers)
		{
			BetterFarming.LOGGER.info(changer.key.toString() + " Has updated.");
			changer.Run(m_seasonClock.currentSeason);
		}
	}

	public static class BiomeEffectChanger
	{

		public final RegistryKey<Biome> key;
		public Biome biome;
		public int[] grassColors;

		public BiomeEffectChanger(RegistryKey<Biome> key)
		{
			this.key = key;
		}

		public void SetBiome(Biome worldBiome)
		{
			this.biome = worldBiome;
		}

		public BiomeEffectChanger SetGrassColors(int[] grassColors)
		{
			this.grassColors = grassColors;
			return this;
		}

		public void Run(int season)
		{
			BiomeEffects effects = biome.getEffects();
			BiomeEffectsAccessor effectsAccessor = (BiomeEffectsAccessor) effects;
			effectsAccessor.fabric_setGrassColor(Optional.of(new Color(255, 0, 0, 255).toInt()));
		}

		private Optional<Integer> CreateOptionalFromArray(int[] array, int season)
		{
			if (season < 0 || season >= array.length)
				return Optional.empty();
			else
				return Optional.of(array[season]);
		}
	}

}
