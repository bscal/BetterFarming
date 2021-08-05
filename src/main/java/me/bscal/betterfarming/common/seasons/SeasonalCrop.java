package me.bscal.betterfarming.common.seasons;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.database.blockdata.BlockData;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class SeasonalCrop
{
	public int maxAge = 1;
	public int baseGrowthTicks;
	public float baseGrowthChance;
	public float growthRate[];
	public List<String> climates;
	public List<Identifier> biomes;

	public boolean ShouldRemove(BlockState state, ServerWorld world, BlockPos pos, Biome blockBiome, BlockData data, int season)
	{
		return !state.isOf(data.block);
	}

	/**
	 * Checks if the crop's growthTime data is capable of growing/incrementing.
	 */
	public boolean CheckGrowingCondition(BlockState state, ServerWorld world, BlockPos pos, Biome blockBiome, BlockData data, int season)
	{
		return growthRate[season] > 0f && data.age < maxAge;
	}

	/**
	 * A check that if the crop can fully grow to the next level/age.
	 */
	public boolean CanFullyGrow(BlockState state, ServerWorld world, BlockPos pos, Biome blockBiome, BlockData data, int season)
	{
		return data.growthTime <= 0 && baseGrowthChance == 0f || BetterFarming.RAND.nextFloat() < baseGrowthChance;
	}

	public void HandleGrowth(BlockState state, ServerWorld world, BlockPos pos, Biome blockBiome, BlockData data, int season,
			int growthAmount)
	{
		data.lastUpdate = BetterFarming.SEASON_CLOCK.ticksSinceCreation;
		data.age = Math.min(maxAge, data.age + growthAmount);
		data.growthTime = baseGrowthTicks;
	}

	public BlockData DefaultBlockData(BlockState state, ServerWorld world, BlockPos pos)
	{
		BlockData data = new BlockData();
		data.growthTime = baseGrowthTicks;
		data.block = state.getBlock();
		return data;
	}

	public static class Builder
	{
		private int maxAge = 1;
		private int baseGrowthTicks;
		private float baseGrowthChance;
		private float growthRate[];
		private List<String> climates;
		private List<Identifier> biomes;

		public Builder SetMaxAge(int max)
		{
			this.maxAge = max;
			return this;
		}

		public Builder SetGrowthTicks(int growthTicks)
		{
			this.baseGrowthTicks = growthTicks;
			return this;
		}

		public Builder SetGrowthChance(float growthChance)
		{
			this.baseGrowthChance = growthChance;
			return this;
		}

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
			crop.maxAge = maxAge;
			crop.baseGrowthTicks = baseGrowthTicks;
			crop.baseGrowthChance = baseGrowthChance;
			crop.growthRate = growthRate;
			crop.biomes = biomes;
			crop.climates = climates;
			return crop;
		}
	}
}