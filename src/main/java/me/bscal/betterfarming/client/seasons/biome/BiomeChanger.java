package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.FastNoiseLite;
import me.bscal.betterfarming.common.seasons.SeasonSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT) public class BiomeChanger implements IBiomeChanger
{

	public static final FastNoiseLite noise = new FastNoiseLite();

	public final RegistryKey<Biome> key;
	private int rawId = -1;
	protected int[] grassColors;
	protected int[] foliageColor;
	protected int[] fallLeaves;

	public BiomeChanger(RegistryKey<Biome> key)
	{
		this.key = key;
		grassColors = new int[4];
		foliageColor = new int[4];
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetSeed(SeasonSettings.Root.Generation.seasonSeed.getValue());
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

	public int GetRandomFallColor(int x, int y)
	{
		int index;

		var leavesGraphics = SeasonSettings.Root.fallLeavesGraphics.getValue();
		if (leavesGraphics == SeasonSettings.FallLeavesSettings.FANCY)
		{
			float val = noise.GetNoise(x, y);
			if (val < -.5f)
				index = 1;
			else if (val > .5f)
				index = 2;
			else if (val < 0f)
				index = 3;
			else
				index = 0;
		}
		else if (leavesGraphics == SeasonSettings.FallLeavesSettings.FAST)
		{
			index = ((x + y) / 16) % fallLeaves.length;
		}
		else
			index = 0;


		return fallLeaves == null ? -1 : fallLeaves[index];
	}
}