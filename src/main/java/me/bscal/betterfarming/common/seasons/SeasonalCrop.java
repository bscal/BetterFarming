package me.bscal.betterfarming.common.seasons;

import me.bscal.betterfarming.common.database.blockdata.BlockData;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class SeasonalCrop
{
	public int minTicks;
	public int maxTicks;
	public int maxAge = 1;
	public float baseGrowthSpeed;
	public float baseGrowthRate;
	public float growthRate[];
	public List<String> climates;
	public List<Identifier> biomes;

	public boolean CheckConditions(BlockState state, ServerWorld world, BlockPos pos, Biome blockBiome, BlockData data)
	{
		// TODO
		return true;
	}

	public static class Builder
	{
		private float growthRate[];
		public List<String> climates;
		public List<Identifier> biomes;

		public Builder SetGrowRates(float... growRates)
		{
			this.growthRate = growRates;
			return this;
		}

		public Builder SetBiomes(Identifier... biomeKeys)
		{
			this.biomes = List.of(biomeKeys);
			return this;
		}

		public Builder SetClimates(String... climateTypes)
		{
			this.climates = List.of(climateTypes);
			return this;
		}

		public SeasonalCrop Build()
		{
			SeasonalCrop crop = new SeasonalCrop();
			crop.growthRate = growthRate;
			return crop;
		}
	}
}