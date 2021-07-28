package me.bscal.betterfarming.common.seasons;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashMap;
import java.util.Map;

public final class Seasons
{

	private Seasons() {}

	public static final int SPRING = 0;
	public static final int SUMMER = 1;
	public static final int AUTUMN = 2;
	public static final int WINTER = 3;

	public static final int WET = 4;
	public static final int DRY = 5;

	public static final SeasonType DESERT = new SeasonType(DRY, DRY, DRY, DRY);
	public static final SeasonType JUNGLE = new SeasonType(WET, WET, WET, WET);
	public static final SeasonType SAVANNA = new SeasonType(WET, WET, DRY, DRY);
	public static final SeasonType BADLANDS = new SeasonType(DRY, DRY, DRY, WET);
	public static final SeasonType BAMBOO_JUNGLE = new SeasonType(WET, WET, WET, DRY);
	public static final SeasonType TUNDRA = new SeasonType(SUMMER, SUMMER, WINTER, WINTER);
	public static final SeasonType SWAMP = new SeasonType(WET, DRY, WET, DRY);

	public static final int MAX_SEASONS = 4;
	public static final Map<RegistryKey<Biome>, SeasonType> SPECIAL_SEASONS = new HashMap<>();

	static
	{
		SPECIAL_SEASONS.put(BiomeKeys.DESERT, DESERT);
		SPECIAL_SEASONS.put(BiomeKeys.JUNGLE, JUNGLE);
	}

	public static int GetSeasonForBiome(RegistryKey<Biome> key, int season)
	{
		if (SPECIAL_SEASONS.containsKey(key))
			return SPECIAL_SEASONS.get(key).GetSeason(season);
		return season;
	}

	public static class SeasonType
	{
		byte[] seasonValues = new byte[4];

		public SeasonType(int spring, int summer, int autumn, int fall)
		{
			seasonValues[0] = (byte) spring;
			seasonValues[1] = (byte) summer;
			seasonValues[2] = (byte) autumn;
			seasonValues[3] = (byte) fall;
		}

		public int GetSeason(int season)
		{
			return seasonValues[Math.min(3, season)];
		}
	}

	/*
	For most safari destinations in Southern Africa
	(Namibia, Botswana, Zambia, Zimbabwe, Madagascar and South Africa),
	 the dry season coincides with the southern hemisphere winter,
	  which typically lasts from April to October. During this time,
	   the weather is typically sunny and cool, warming up as you move
	    into the months of September/October.  The wet season typically runs
	     from November to March, which is also the hottest and most humid time of year.

	In East Africa (Uganda, Rwanda, Kenya and Tanzania) the dry season
	 is from late June to September.  The region typically experiences
	  two wet seasons.  The main wet season lasts from April to early June,
	   and there is a more sporadic wet season from October to December.
	*/
}
