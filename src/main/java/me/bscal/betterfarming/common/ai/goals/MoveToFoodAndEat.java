package me.bscal.betterfarming.common.ai.goals;

import me.bscal.betterfarming.common.components.entity.EntityEcoProvider;
import me.bscal.betterfarming.common.components.entity.IEntityEcoComponent;
import me.bscal.betterfarming.common.components.entity.types.AnimalEcoComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MoveToFoodAndEat extends MoveToTargetPosGoal
{

	private final IEntityEcoComponent m_ecoComponent;

	public MoveToFoodAndEat(PathAwareEntity entity, double speed, int range)
	{
		super(entity, speed, range);
		this.m_ecoComponent = EntityEcoProvider.ENTITY_ECO.get(entity);
	}

	@Override
	public boolean canStart()
	{
		if (m_ecoComponent == null)
			return false;

		return IsHungry() && super.canStart();
	}

	@Override
	public void start()
	{
		super.start();

	}

	@Override
	public void tick()
	{
		super.tick();

		if (this.hasReached())
		{
			World world = this.mob.world;
			BlockState state = world.getBlockState(this.targetPos);
			if (state.isOf(Blocks.HAY_BLOCK))
			{
				world.sendEntityStatus(this.mob, (byte) 10);
				m_ecoComponent.EatFood(10);
				world.setBlockState(this.targetPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
			}
		}
	}

	@Override
	public boolean shouldContinue()
	{
		return super.shouldContinue();
	}

	@Override
	public double getDesiredSquaredDistanceToTarget()
	{
		return 2.0D;
	}

	@Override
	protected int getInterval(PathAwareEntity mob)
	{
		return 200 + mob.getRandom().nextInt(200);
	}

	@Override
	protected boolean isTargetPos(WorldView world, BlockPos pos)
	{
		return IsAnEatableBlock(world, pos);
	}

	private boolean IsHungry()
	{
		return m_ecoComponent.GetHunger() < 20 - this.mob.getRandom().nextInt(6) + 2;
	}

	private boolean IsAnEatableBlock(WorldView world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		if (state.isOf(Blocks.HAY_BLOCK))
		{
			return true;
		}
		return false;
	}

}
