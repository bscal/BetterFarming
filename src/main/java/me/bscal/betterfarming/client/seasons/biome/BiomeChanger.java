package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.BetterFarming;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT) public class BiomeChanger implements IBiomeChanger
{

	public final RegistryKey<Biome> key;
	public int rawId = -1;
	public int[] grassColors;
	public int[] foliageColor;

	public BiomeChanger(RegistryKey<Biome> key)
	{
		this.key = key;
		grassColors = new int[4];
		foliageColor = new int[4];
	}

	@Override
	public void InitChanger(Biome biome)
	{
		rawId = BetterFarming.SEASONS_REGISTRY.seasonDataMap.getRawId(biome);
	}

	@Override
	public Biome GetBiome()
	{
		return MinecraftClient.getInstance().world.getRegistryManager().get(Registry.BIOME_KEY).get(key);
	}

	@Override
	public int GetColor(int season)
	{
		return grassColors[season];
	}

	@Override
	public int GetFoliageColor(int season)
	{
		return foliageColor[season];
	}
}