package me.bscal.betterfarming.common.components.chunk;

import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import me.bscal.betterfarming.common.components.ClimateType;
import me.bscal.betterfarming.common.components.GrowthRates;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkEcoComponent implements IChunkEcoComponent, ServerTickingComponent
{

	public final WorldChunk chunk;
	public int maxLandUseValue = 256;
	public int currentLandUseValue = 0;
	public int nutrition = 100;
	public int fertilizer = 0;
	public int arability = 0;
	public int irrigation = 0;
	public GrowthRates growthRates = new GrowthRates();
	public ClimateType climateType;

	private int m_tickTimer;

	public ChunkEcoComponent(Chunk chunk)
	{
		this.chunk = (chunk instanceof WorldChunk) ? (WorldChunk) chunk : null;
		//this.chunk.getBiomeArray().getBiomeForNoiseGen(chunk.getPos());
	}

	@Override
	public void serverTick()
	{
		if (m_tickTimer++ % 100 == 0)
		{
			m_tickTimer -= 100;
		}
	}

	@Override
	public int GetMaxUseValue()
	{
		return maxLandUseValue;
	}

	@Override
	public int GetUseValue()
	{
		return currentLandUseValue;
	}

	@Override
	public int GetArability()
	{
		return arability;
	}

	@Override
	public int GetNutrition()
	{
		return nutrition;
	}

	@Override
	public int GetFertilizer()
	{
		return fertilizer;
	}

	@Override
	public int GetIrrigation()
	{
		return irrigation;
	}

	@Override
	public GrowthRates GetGrowthRates()
	{
		return growthRates;
	}

	@Override
	public ClimateType GetClimateType()
	{
		return climateType;
	}

	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if (tag.contains("eco"))
		{
			NbtCompound ecoTag = tag.getCompound("eco");
			maxLandUseValue = ecoTag.getInt("maxLandUse");
			currentLandUseValue = ecoTag.getInt("currentLandUse");
			nutrition = ecoTag.getInt("nutrition");
			fertilizer = ecoTag.getInt("fertilizer");
			arability = ecoTag.getInt("arability");
			growthRates.FromTag(ecoTag);
			//climateType = ClimateType.REGISTRY.getOrDefault(ecoTag.getString("climateType"),
					//ClimateType.GENERIC);
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag)
	{
		NbtCompound ecoTag = tag.getCompound("eco");
		ecoTag.putInt("maxLandUse", maxLandUseValue);
		ecoTag.putInt("currentLandUse", currentLandUseValue);
		ecoTag.putInt("nutrition", nutrition);
		ecoTag.putInt("fertilizer", fertilizer);
		ecoTag.putInt("arability", arability);
		growthRates.ToTag(ecoTag);
		//ecoTag.putString("climateType", climateType.name);
	}
}
