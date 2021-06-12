package me.bscal.betterfarming.common.ai.goals;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MoveToAndDrinkGoal extends MoveToAndConsumeGoal
{
	public MoveToAndDrinkGoal(PathAwareEntity entity, double speed, int range, boolean checkPriority)
	{
		super(entity, speed, range, 2, checkPriority);
	}

	@Override
	protected void OnConsumedSuccess(World world, BlockPos pos, BlockState state)
	{
		m_ecoComponent.Drink(10);
	}

	@Override
	protected boolean CanStartConsume()
	{
		return m_ecoComponent.IsThirsty();
	}

	@Override
	protected boolean IsTargetPosConsumable(WorldView world, BlockPos pos, BlockState state)
	{
		return state.isOf(Blocks.WATER);
	}
}
