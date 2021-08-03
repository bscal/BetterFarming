package me.bscal.betterfarming.common.seasons;

import net.minecraft.util.Identifier;

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