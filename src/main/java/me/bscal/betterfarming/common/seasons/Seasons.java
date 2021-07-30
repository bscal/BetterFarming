package me.bscal.betterfarming.common.seasons;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.RegistryMapToObject;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashMap;
import java.util.Map;

public final class Seasons
{

	private Seasons()
	{
	}

	public static final int SPRING = 0;
	public static final int SUMMER = 1;
	public static final int AUTUMN = 2;
	public static final int WINTER = 3;

	public static final int WET = 0;
	public static final int DRY = 1;

	public static final SeasonType DESERT = new SeasonType(DRY, DRY, DRY, DRY, true);
	public static final SeasonType JUNGLE = new SeasonType(WET, WET, WET, WET, true);
	public static final SeasonType SAVANNA = new SeasonType(WET, WET, DRY, DRY, true);
	public static final SeasonType BADLANDS = new SeasonType(DRY, DRY, DRY, WET, true);
	public static final SeasonType BAMBOO_JUNGLE = new SeasonType(WET, WET, WET, DRY, true);
	public static final SeasonType TUNDRA = new SeasonType(SUMMER, SUMMER, WINTER, WINTER, false);
	public static final SeasonType SWAMP = new SeasonType(WET, DRY, WET, DRY, true);

	public static final int MAX_SEASONS = 4;
	public static final Map<RegistryKey<Biome>, SeasonType> SPECIAL_SEASONS = new HashMap<>();

	public static RegistryMapToObject<Biome, SeasonType> SEASONS_MAP;

	static
	{
		SPECIAL_SEASONS.put(BiomeKeys.DESERT, DESERT);
		SPECIAL_SEASONS.put(BiomeKeys.JUNGLE, JUNGLE);
	}

	/**
	 * Initilizes the season map. This is done because we need to access World instance.
	 * Ran on both server and clients
	 */
	public static void InitSeasonsMap(World world)
	{
		SEASONS_MAP = new RegistryMapToObject<>(world, Registry.BIOME_KEY);
		SEASONS_MAP.putFromRegistryKey(BiomeKeys.DESERT, DESERT);
		SEASONS_MAP.putFromRegistryKey(BiomeKeys.JUNGLE, JUNGLE);
	}

	public static int GetSeasonForBiome(RegistryKey<Biome> key, int season)
	{
		if (SPECIAL_SEASONS.containsKey(key))
			return SPECIAL_SEASONS.get(key).GetSeason(season);
		return season;
	}

	public static String GetNameOfSeason(int season)
	{
		return new TranslatableText(BetterFarming.MOD_ID + ".season" + season).getString();

	}

	public static String GetNameOfSeasonByBiome(RegistryKey<Biome> key, int season)
	{
		if (SPECIAL_SEASONS.containsKey(key))
		{
			SeasonType type = SPECIAL_SEASONS.get(key);
			if (type.isTropical)
				return new TranslatableText(BetterFarming.MOD_ID + ((type.seasonValues[season] == WET) ?
						".wet" :
						".dry")).getString();
		}
		return GetNameOfSeason(season);
	}

	public static class SeasonType
	{
		public final boolean isTropical;
		public final byte[] seasonValues = new byte[4];
		public Tag<Block> growables;

		public SeasonType(int spring, int summer, int autumn, int fall, boolean isTropical)
		{
			seasonValues[0] = (byte) spring;
			seasonValues[1] = (byte) summer;
			seasonValues[2] = (byte) autumn;
			seasonValues[3] = (byte) fall;
			this.isTropical = isTropical;
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
