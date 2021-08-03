package me.bscal.betterfarming.common.seasons;

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashSet;
import java.util.Set;

public class ClimateType
{

	// TODO probably move to class only containing biome sets and stuff
	public static final Set<Identifier> TROPICAL_BIOMES = new HashSet<>();
	public static final Set<Identifier> DRY_BIOMES = new HashSet<>();
	public static final Set<Identifier> MILD_MID_LATITUDE_BIOMES = new HashSet<>();
	public static final Set<Identifier> COLD_MID_LATITUDE_BIOMES = new HashSet<>();
	public static final Set<Identifier> POLAR_BIOMES = new HashSet<>();

	public static Set<Identifier> TypeByString(String setName)
	{
		try
		{
			return (Set<Identifier>) ClimateType.class.getField(setName.toUpperCase()).get(null);
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	static
	{
		TROPICAL_BIOMES.add(BiomeKeys.JUNGLE.getValue());
	}

	public static final ClimateType RAINFOREST = new ClimateType();
	public static final ClimateType SAVANNA = new ClimateType();
	public static final ClimateType DESERT = new ClimateType();
	public static final ClimateType SEMI_ARID = new ClimateType();
	public static final ClimateType HUMID_SUBTROPICAL = new ClimateType();
	public static final ClimateType OCEANIC = new ClimateType();
	public static final ClimateType MEDITERRANEAN = new ClimateType();
	public static final ClimateType HUMID_CONTINENTAL = new ClimateType();
	public static final ClimateType SUBARCTIC = new ClimateType();
	public static final ClimateType TUNDRA = new ClimateType();
	public static final ClimateType ICE_CAP = new ClimateType();
	public static final ClimateType ALPINE = new ClimateType();


}
