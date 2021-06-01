package me.bscal.betterfarming.common.components;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

public class ClimateType
{

	public static final ClimateType GENERIC = new ClimateType("Generic", -1, 50, 50, 1.0f);
	public static final ClimateType TEMPERATE = new ClimateType("Temperate", -1, 50, 50, 1.2f);

	public static final Map<String, ClimateType> REGISTRY = new HashMap<>();
	public static final Map<Integer, ClimateType> BIOME_CLIMATE_TYPE_MAP = new HashMap<>();

	static
	{
		REGISTRY.put(GENERIC.name, GENERIC);
		REGISTRY.put(TEMPERATE.name, TEMPERATE);

		BIOME_CLIMATE_TYPE_MAP.put(BiomeIds.PLAINS, TEMPERATE);
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

	public static ClimateType GetTypeFromBiome(final Chunk chunk)
	{
		BiomeArray biomeArray = chunk.getBiomeArray();
		if (biomeArray == null)
			return GENERIC;
		Biome biome = biomeArray.getBiomeForNoiseGen(chunk.getPos());
		return BIOME_CLIMATE_TYPE_MAP.getOrDefault(biome, GENERIC);
	}

}
