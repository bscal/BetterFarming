package me.bscal.betterfarming.client.seasons.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public interface IBiomeChanger
{

	void InitChanger(Biome biome);
	Biome GetBiome();
	int GetColor(int season);
	int GetFoliageColor(int season);

}
