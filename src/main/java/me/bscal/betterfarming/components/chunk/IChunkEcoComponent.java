package me.bscal.betterfarming.components.chunk;

import dev.onyxstudios.cca.api.v3.component.Component;
import me.bscal.betterfarming.components.ClimateType;
import me.bscal.betterfarming.components.GrowthRates;
import net.minecraft.world.chunk.WorldChunk;

public interface IChunkEcoComponent extends Component
{

	int GetMaxUseValue();

	int GetUseValue();

	int GetArability();

	int GetNutrition();

	int GetFertilizer();

	int GetIrrigation();

	GrowthRates GetGrowthRates();

	ClimateType GetClimateType();

}
