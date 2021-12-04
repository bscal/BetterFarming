package me.bscal.betterfarming.common.components;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class ClimateType
{

	public static final ClimateType GENERIC = new ClimateType("Generic", -1, 50, 50, 1.0f);
	public static final ClimateType TEMPERATE = new ClimateType("Temperate", -1, 50, 50, 1.2f);

	public static final Map<String, ClimateType> REGISTRY = new HashMap<>();
	public static final Map<String, ClimateType> BIOME_CLIMATE_TYPE_MAP = new HashMap<>();

	static
	{
		REGISTRY.put(GENERIC.name, GENERIC);
		REGISTRY.put(TEMPERATE.name, TEMPERATE);

		BIOME_CLIMATE_TYPE_MAP.put("minecraft:plains", TEMPERATE);
	}

	public final String name;
	public final int biome;
	public final int wetness;
	public final int temperature;
	public final float bonus;

	public ClimateType(String name, int biome, int wetness, int temp, float bonus)
	{
		this.name = name;
		this.biome = biome;
		this.wetness = wetness;
		this.temperature = temp;
		this.bonus = bonus;
	}

	public static ClimateType GetTypeFromBiome(final World world, final BlockPos pos)
	{
		Biome biome = world.getBiome(pos);
		return BIOME_CLIMATE_TYPE_MAP.getOrDefault(biome.toString(), GENERIC);
	}

}
