package me.bscal.betterfarming.common.seasons;

import me.bscal.betterfarming.common.database.blockdata.blocks.CropDataBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class SeasonalCrop
{
	/**
	 * The amount of ticks on average it takes for a block's randomTick() method to be called on setting 3<br>
	 * Useful to get an estimated time for growths.<br>
	 */
	public static final float RANDOM_TICK_DEFAULT_AVERAGE = 1365.33333f;

	public int maxAge;
	public float maxGrowth;
	public float[] growthPerAge;
	public float growthRate;
	public float[] seasonGrowthRates;

	public IntProperty agePropertyRef;
	public List<String> climates;
	public List<Identifier> biomes;

	public boolean IsMaxGrowth(BlockState state, CropDataBlock data)
	{
		return data.age >= maxAge || (state.contains(agePropertyRef) && state.get(agePropertyRef) >= maxAge);
	}

	public boolean ShouldRemove(BlockState state, ServerWorld world, BlockPos pos, Biome blockBiome, CropDataBlock data, int season)
	{
		return !state.isOf(data.block);
	}

	/**
	 * Checks if the crop's growthTime data is capable of growing/incrementing.
	 */
	public boolean TestGrowingCondition(BlockState state, ServerWorld world, BlockPos pos, Biome blockBiome, CropDataBlock data, int season)
	{
		return growthRate > 0f && data.growthModifier > 0f && seasonGrowthRates[season] > 0f && !IsMaxGrowth(state, data);
	}

	/**
	 * A check that if the crop can fully grow to the next level/age.
	 */
	public boolean TickGrowth(BlockState state, ServerWorld world, BlockPos pos, Biome blockBiome, CropDataBlock data, int season)
	{
		ApplyGrowth(data, season, 0, 0);

		// Incase the block is unloaded, you can still progress growth time. This will handle
		// skipping to the correct age.
		if (data.age <= maxAge && data.totalGrowthReceived > maxGrowth && state.contains(agePropertyRef))
		{
			// TODO handle non mag age skips
			world.setBlockState(pos, state.with(agePropertyRef, maxAge));
		}

		return data.currentAgeGrowthReceived > GetGrowthForAge(data.age);
	}

	/**
	 * Safer then directly accessing the growthRates array. Since this contains some safety checks and allows you to set 1 values for all ages.
	 * TODO probably the correct thing is to turn growthRates into a class.
	 */
	public float GetGrowthForAge(int age)
	{
		// Allows to set one value so all ages are the same
		if (age <= maxAge && age > 1 && growthPerAge.length == 1)
			return growthPerAge[0];
		if (age >= growthPerAge.length || age < 0)
			return 0f;
		return growthPerAge[age];
	}

	public float GetGrowthRemaining(CropDataBlock data)
	{
		float neededGrowth = 0;
		for (int i = data.age; i < growthPerAge.length; i++)
			neededGrowth += GetGrowthForAge(i);
		return neededGrowth;
	}

	public float GetGrowthFromAge(int age)
	{
		float neededGrowth = 0;
		for (int i = age; i < growthPerAge.length; i++)
			neededGrowth += GetGrowthForAge(i);
		return neededGrowth;
	}

	public void OnGrow(BlockState state, ServerWorld world, BlockPos pos, CropDataBlock data, int growthAmount)
	{
		data.age = Math.min(maxAge, data.age + growthAmount);
	}

	/**
	 * Processes growth for the block. Does nothing else but calculates the growth and add to the blockData's values.
	 */
	public void ApplyGrowth(CropDataBlock data, int season, float flatAmount, float percentageAmount)
	{
		float growth = (growthRate * (seasonGrowthRates[season] + data.growthModifier + percentageAmount)) + flatAmount;
		data.totalGrowthReceived += growth;
		data.currentAgeGrowthReceived += growth;
	}

	/**
	 * Adds growth and attempts to progress age and update blockstate.
	 */
	public void AddGrowth(BlockState state, ServerWorld world, BlockPos pos, CropDataBlock data, float growth)
	{
		data.totalGrowthReceived += growth;

		float neededGrowth = GetGrowthForAge(data.age);
		float remainingGrowth = neededGrowth - growth;
		if (!IsMaxGrowth(state, data) && remainingGrowth > 0)
		{
			data.currentAgeGrowthReceived = remainingGrowth;
			OnGrow(state, world, pos, data, 1);
			if (agePropertyRef != null)
				world.setBlockState(pos, state.with(agePropertyRef, data.age + 1), Block.NOTIFY_ALL);
		}
		else
		{
			data.currentAgeGrowthReceived += growth;
		}
	}

	public static class Builder
	{
		private int maxAge;
		private float maxGrowth;
		private float[] growthPerAge;
		private float growthRate;
		private float[] seasonGrowthRates;
		public IntProperty agePropertyRef;
		private List<String> climates;
		private List<Identifier> biomes;

		public Builder(int maxAge, float... growthPerAge)
		{
			this.maxAge = maxAge;
			this.growthRate = 1.0f;
			this.growthPerAge = growthPerAge;
			for (float f : growthPerAge)
				maxGrowth += f;
			this.seasonGrowthRates = new float[] { 1.0f, 1.0f, 1.0f, 0.0f };
		}

		public Builder SetAgeProp(IntProperty ageProperty)
		{
			this.agePropertyRef = ageProperty;
			return this;
		}

		public Builder SetMaxAge(int max)
		{
			this.maxAge = max;
			return this;
		}

		public Builder SetGrowthRate(float growthRate)
		{
			this.growthRate = growthRate;
			return this;
		}

		public Builder SetMaxGrowth(int maxGrowth)
		{
			this.maxGrowth = maxGrowth;
			return this;
		}

		public Builder SetGrowthPerAge(float... growthPerAge)
		{
			this.growthPerAge = growthPerAge;
			return this;
		}

		public Builder SetSeasonGrowthRates(float... seasonGrowthRates)
		{
			this.seasonGrowthRates = seasonGrowthRates;
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
			crop.maxGrowth = maxGrowth;
			crop.seasonGrowthRates = seasonGrowthRates;
			crop.growthPerAge = growthPerAge;
			crop.growthRate = growthRate;
			crop.biomes = biomes;
			crop.climates = climates;
			return crop;
		}
	}
}