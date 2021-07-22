package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.biome.modification.BiomeEffectsAccessor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;

import java.util.Optional;

@Environment(EnvType.CLIENT) public class BiomeChanger
{

	public final RegistryKey<Biome> key;
	public final Biome biome;
	public int[] grassColors;

	public BiomeChanger(RegistryKey<Biome> key, ClientWorld clientWorld)
	{
		this.key = key;
		this.biome = clientWorld.getRegistryManager().get(Registry.BIOME_KEY).get(key);
	}

	public BiomeChanger SetGrassColors(int[] grassColors)
	{
		this.grassColors = grassColors;
		return this;
	}

	public int GetColor(int season)
	{
		return new Color(BetterFarming.RAND.nextInt(255), BetterFarming.RAND.nextInt(255), 0, 255).toInt();
	}

	private Optional<Integer> CreateOptionalFromArray(int[] array, int season)
	{
		if (season < 0 || season >= array.length)
			return Optional.empty();
		else
			return Optional.of(array[season]);
	}
}