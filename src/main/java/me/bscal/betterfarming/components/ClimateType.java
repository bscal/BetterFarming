package me.bscal.betterfarming.components;

import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

public class ClimateType
{

	public static final ClimateType GENERIC = new ClimateType("Generic", -1, 50, 50, 1.0f);

	public static final Map<String, ClimateType> REGISTRY = new HashMap<>();

	static
	{
		REGISTRY.put(GENERIC.name, GENERIC);
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
		return GENERIC;
	}

}
