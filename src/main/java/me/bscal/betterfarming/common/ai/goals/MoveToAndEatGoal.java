package me.bscal.betterfarming.common.ai.goals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MoveToAndEatGoal extends MoveToAndConsumeGoal
{
	public MoveToAndEatGoal(PathAwareEntity entity, double speed, int range)
	{
		super(entity, speed, range, 2, true);
	}

	@Override
	protected void OnConsumedSuccess(World world, BlockPos pos, BlockState state)
	{
		world.sendEntityStatus(this.mob, (byte) 10);
		if (state.isOf(Blocks.GRASS_BLOCK))
			world.setBlockState(this.targetPos, Blocks.DIRT.getDefaultState(), Block.NOTIFY_ALL);
		else
			world.setBlockState(this.targetPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
		m_ecoComponent.EatFood(0);
	}

	@Override
	protected boolean CanStartConsume()
	{
		return m_ecoComponent.IsHungry();
	}

	@Override
	protected boolean IsTargetPosConsumable(WorldView world, BlockPos pos, BlockState state)
	{
		return m_ecoComponent.GetEatableBlocks(world, pos, state);
	}
}
