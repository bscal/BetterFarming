package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.mixin.world.ClientWorldAccessor;
import me.bscal.betterfarming.common.seasons.SeasonClock;
import me.bscal.betterfarming.common.utils.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.mixin.biome.modification.BiomeEffectsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class BiomeEffectHandler
{

	private final SeasonClock m_seasonClock = new SeasonClock();
	private final List<BiomeEffectChanger> m_biomeEffectChangers = new ArrayList<>();
	private boolean m_firstUpdate = true;

	public BiomeEffectHandler()
	{
		Register(BiomeChangers.PLAINS_CHANGER);
	}

	public void Register(BiomeEffectChanger changer)
	{
		m_biomeEffectChangers.add(changer);
	}

	public ClientPlayNetworking.PlayChannelHandler SyncTime()
	{
		return (client, handler, buf, responseSender) ->
		{
			long ticks = buf.readLong();
			int season = buf.readInt();
			m_seasonClock.ticksSinceCreation = ticks;
			m_seasonClock.currentSeason = season;
			if (m_seasonClock.ticksSinceCreation % 100 == 0)
			{
				UpdateSeasonColors();
				//MinecraftClient.getInstance().world.reloadColor();
				((ClientWorldAccessor)MinecraftClient.getInstance().world).getColorCache().put(BiomeColors.GRASS_COLOR, new BiomeColorCache());
			}
		};
	}

	public ClientPlayNetworking.PlayChannelHandler SyncSeasonChange()
	{
		return (client, handler, buf, responseSender) -> {
			m_seasonClock.currentSeason = buf.readInt();
			UpdateSeasonColors();
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
		public final Biome biome;
		public int[] grassColors;

		public BiomeEffectChanger(RegistryKey<Biome> key)
		{
			this.key = key;
			this.biome = BuiltinRegistries.BIOME.get(key);
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
			effectsAccessor.fabric_setGrassColor(Optional.of(new Color(255, 0, 0, 0).toInt()));
			BetterFarming.LOGGER.info(effects.getGrassColor());
			BetterFarming.LOGGER.info(biome.getGrassColorAt(0,0));
			BetterFarming.LOGGER.info(new Color(grassColors[0]).toInt());
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
