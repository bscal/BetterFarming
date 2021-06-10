package me.bscal.betterfarming.common.ai.goals;

import me.bscal.betterfarming.common.components.entity.EntityEcoProvider;
import me.bscal.betterfarming.common.components.entity.IEntityEcoComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class MoveToAndConsumeGoal extends MoveToTargetPosGoal
{

	protected final IEntityEcoComponent m_ecoComponent;
	protected final int m_range;
	protected final int m_maxYDifference;
	private boolean m_shouldStop;

	public MoveToAndConsumeGoal(PathAwareEntity entity, double speed, int range, int maxYDifference)
	{
		super(entity, speed, range, maxYDifference);
		this.m_range = range;
		this.m_maxYDifference = maxYDifference;
		this.m_ecoComponent = EntityEcoProvider.ENTITY_ECO.get(entity);
	}

	@Override
	public boolean canStart()
	{
		return m_ecoComponent != null && CanStartConsume() && super.canStart();
	}

	@Override
	public void stop()
	{
		super.stop();
		m_shouldStop = false;
	}

	@Override
	public boolean shouldContinue()
	{
		return !m_shouldStop && super.shouldContinue();
	}

	@Override
	public void tick()
	{
		super.tick();

		this.mob.getLookControl()
				.lookAt((double) this.targetPos.getX() + 0.5D, this.targetPos.getY() + 1,
						(double) this.targetPos.getZ() + 0.5D, 10.0F, (float) this.mob.getLookPitchSpeed());
		if (this.hasReached())
		{
			World world = this.mob.world;
			BlockState state = world.getBlockState(this.targetPos);
			if (IsTargetPosConsumable(world, this.targetPos, state))
			{
				OnConsumedSuccess(world, this.targetPos, state);
			}
		}

		if (this.mob.getNavigation().getCurrentPath() == null || !this.mob.getNavigation()
				.getCurrentPath()
				.reachesTarget())
		{
			m_shouldStop = true;
			this.cooldown = 40;
		}
	}

	@Override
	public double getDesiredSquaredDistanceToTarget()
	{
		return 1.0D;
	}

	@Override
	protected boolean findTargetPos()
	{
		// TODO is this worth it and any good?
		BlockPos blockPos = this.mob.getBlockPos();
		BlockPos posResult = null;
		int currentPriority = Integer.MAX_VALUE;
		int halfRange = m_range / 2;
		for (BlockPos pos : BlockPos.iterateRandomly(this.mob.getRandom(), 32, blockPos.getX() - halfRange,
				blockPos.getY() - 2, blockPos.getZ() - halfRange, blockPos.getX() + halfRange,
				blockPos.getY() + 2, blockPos.getZ() + halfRange))
		{
			if (this.mob.isInWalkTargetRange(pos) && this.isTargetPos(this.mob.world, pos))
			{
				// TODO Cache the blockstate? Have basic distance var to eat closest of prio?
				int prio = m_ecoComponent.GetConsumablesPriority(pos);
				if (prio < currentPriority)
				{
					currentPriority = prio;
					posResult = pos.toImmutable();
					if (prio < 1)
						break;
				}
			}
		}
		if (posResult != null)
		{
			this.targetPos = posResult;
			return true;
		}
		return false;
	}

	@Override
	protected boolean isTargetPos(WorldView world, BlockPos pos)
	{
		return IsTargetPosConsumable(world, pos, world.getBlockState(pos));
	}

	protected abstract void OnConsumedSuccess(World world, BlockPos pos, BlockState state);

	protected abstract boolean CanStartConsume();

	protected abstract boolean IsTargetPosConsumable(WorldView world, BlockPos pos, BlockState state);

}
