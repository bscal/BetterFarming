package me.bscal.betterfarming.common.components.entity;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface IEntityEcoComponent extends Component
{

	int GetGrowthRate();

	int GetGrowthStage();

	int GetHappiness();

	int GetOvercrowdedness();

	int GetHunger();

	int GetYieldGrade();

}
