package me.bscal.betterfarming.common.seasons;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SeasonCropManager
{

	private final Map<Identifier, SeasonalCrop> seasonalCrops = new HashMap<>();


	public static class SeasonalCrop
	{
		public float growRate[];
		public int season[];

		SeasonalCrop()
		{
			growRate = new float[Seasons.MAX_SEASONS];
			season = new int[Seasons.MAX_SEASONS];
		}
	}

	public static class TropicalCrop extends SeasonalCrop
	{
		TropicalCrop()
		{
			growRate = new float[2];
			season = new int[2];
		}
	}

}
