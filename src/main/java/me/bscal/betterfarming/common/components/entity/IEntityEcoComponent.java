package me.bscal.betterfarming.common.components.entity;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public interface IEntityEcoComponent extends ComponentV3, ServerTickingComponent
{

	boolean GetEatableBlocks(WorldView world, BlockPos pos, BlockState state);

	int GetConsumablesPriority(BlockPos blockPos, BlockState state);

	boolean IsHungry();

	boolean IsThirsty();

	void EatFood(int value);

	void Drink(int value);

	float GetGrowthRate();

	int GetGrowthStage();

	int GetHappiness();

	int GetCondition();

	int GetFatness();

	int GetHunger();

	int GetThirst();

	boolean IsOvercrowded();

}
