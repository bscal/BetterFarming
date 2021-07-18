package me.bscal.betterfarming.common.seasons.biome;

import me.bscal.betterfarming.common.events.SeasonEvents;
import net.fabricmc.fabric.mixin.biome.modification.BiomeEffectsAccessor;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BiomeEffectHandler implements SeasonEvents.SeasonChanged
{

	private final List<BiomeEffectChanger> m_biomeEffectChangers = new ArrayList<>();

	public BiomeEffectHandler()
	{
		SeasonEvents.SEASON_CHANGED.register(this);

		Register(new BiomeEffectChanger(BiomeKeys.PLAINS).SetGrassColors(new Integer[] {} ));
	}

	public void Register(BiomeEffectChanger changer)
	{
		m_biomeEffectChangers.add(changer);
	}

	@Override
	public void OnSeasonChanged(int newSeason, long currentDay)
	{
		for (BiomeEffectChanger changer : m_biomeEffectChangers)
		{
			changer.Run(newSeason);
		}
	}

	public static class BiomeEffectChanger
	{

		public final RegistryKey<Biome> key;
		public final Biome biome;
		public Integer[] grassColors;

		public BiomeEffectChanger(RegistryKey<Biome> key)
		{
			this.key = key;
			this.biome = BuiltinRegistries.BIOME.get(key);
		}

		public BiomeEffectChanger SetGrassColors(Integer[] grassColors)
		{
			this.grassColors = grassColors;
			return this;
		}

		public void Run(int season)
		{
			BiomeEffects effects = biome.getEffects();
			BiomeEffectsAccessor effectsAccessor = (BiomeEffectsAccessor) effects;
			effectsAccessor.fabric_setGrassColor(CreateOptionalFromArray(grassColors, season));
		}

		private <T> Optional<T> CreateOptionalFromArray(T[] array, int season)
		{
			if (season < 0 || season >= array.length)
				return Optional.empty();
			else
				return Optional.of(array[season]);
		}
	}

}
