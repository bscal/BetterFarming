package me.bscal.betterfarming.common.components.entity;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;

public interface IEntityEcoComponent extends ComponentV3, ServerTickingComponent
{

	float GetGrowthRate();

	int GetGrowthStage();

	int GetHappiness();

	int GetCondition();

	int GetFatness();

	int GetHunger();

	int GetThirst();

	boolean IsOvercrowded();

}
