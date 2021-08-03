package me.bscal.betterfarming.common.seasons;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.RegistryMapToObject;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class SeasonsRegistry
{

	public RegistryMapToObject<Biome, Seasons.SeasonType> seasonDataMap;
	public Int2ObjectOpenHashMap<ClimateType> climateMap;

	public void Load(World world)
	{
		if (seasonDataMap != null)
			return;
		seasonDataMap = new RegistryMapToObject<>(world, Registry.BIOME_KEY);
		climateMap = new Int2ObjectOpenHashMap<>();
		climateMap.defaultReturnValue();
		Register();
		BetterFarming.LOGGER.info("Registered Seasons.");
	}

	/**
	 * Just to make sure when you exit a world any references are freed. This should only matter for clients.
	 */
	public void Unload()
	{
		seasonDataMap = null;
	}

	protected void Register()
	{

	}

}
