package me.bscal.betterfarming.common.components.chunk;

import dev.onyxstudios.cca.api.v3.component.Component;
import me.bscal.betterfarming.common.components.ClimateType;
import me.bscal.betterfarming.common.components.GrowthRates;

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
