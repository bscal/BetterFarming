package me.bscal.betterfarming.client.seasons.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT) public class BiomeChanger
{

	public final RegistryKey<Biome> key;
	public final Biome biome;
	public int[] grassColors;
	public int[] foliageColor;

	public BiomeChanger(RegistryKey<Biome> key, ClientWorld clientWorld)
	{
		this.key = key;
		this.biome = clientWorld.getRegistryManager().get(Registry.BIOME_KEY).get(key);
	}

	public int GetColor(int season)
	{
		return grassColors[season];
	}

	public int GetFoliageColor(int season)
	{
		return foliageColor[season];
	}
}