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
	protected final boolean m_checkPriority;
	protected BlockState m_cachedState;
	private boolean m_shouldStop;
	private int m_cantReachTicks;

	public MoveToAndConsumeGoal(PathAwareEntity entity, double speed, int range, int maxYDifference,
			boolean checkPriority)
	{
		super(entity, speed, range, maxYDifference);
		this.m_range = range;
		this.m_maxYDifference = maxYDifference;
		this.m_checkPriority = checkPriority;
		this.m_ecoComponent = EntityEcoProvider.ENTITY_ECO.get(entity);
	}

	protected abstract void OnConsumedSuccess(World world, BlockPos pos, BlockState state);

	protected abstract boolean CanStartConsume();

	protected abstract boolean IsTargetPosConsumable(WorldView world, BlockPos pos, BlockState state);

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
		return !m_shouldStop && IsTargetPosConsumable(this.mob.world, this.targetPos,
				m_cachedState) && super.shouldContinue();
	}

	@Override
	public void tick()
	{
		super.tick();

		this.mob.getLookControl()
				.lookAt((double) this.targetPos.getX() + 0.5D, this.targetPos.getY() + 1,
						(double) this.targetPos.getZ() + 0.5D, 10.0F, (float) this.mob.getPitch());

		if (this.hasReached() && IsTargetPosConsumable(this.mob.world, this.targetPos, m_cachedState))
			Success();

		if (this.mob.getNavigation().getCurrentPath() == null || !this.mob.getNavigation()
				.getCurrentPath()
				.reachesTarget() && m_cantReachTicks++ > 60)
		{
			// Checks for floating blocks before stopping.
			for (int i = 0; i < m_maxYDifference; i++)
			{
				if (this.targetPos.down(1 + i).isWithinDistance(this.mob.getPos(), getDesiredSquaredDistanceToTarget()))
				{
					Success();
					return;
				}
			}

			// Stops the goal from running since target is unreachable
			m_cantReachTicks = 0;
			this.cooldown = 60;
			m_shouldStop = true;
		}
	}

	@Override
	public double getDesiredSquaredDistanceToTarget()
	{
		return 2.0D;
	}

	@Override
	protected boolean findTargetPos()
	{
		// TODO is this worth it and any good?
		BlockPos blockPos = this.mob.getBlockPos();
		BlockPos posResult = null;
		int currentPriority = Integer.MAX_VALUE;
		double currentDistance = Integer.MAX_VALUE;
		int halfRange = m_range / 2;
		for (BlockPos pos : BlockPos.iterateRandomly(this.mob.getRandom(),
				m_range * m_range * m_maxYDifference, blockPos.getX() - halfRange,
				blockPos.getY() - m_maxYDifference, blockPos.getZ() - halfRange, blockPos.getX() + halfRange,
				blockPos.getY() + m_maxYDifference, blockPos.getZ() + halfRange))
		{
			m_cachedState = this.mob.world.getBlockState(pos);
			if (this.mob.isInWalkTargetRange(pos) && this.isTargetPos(this.mob.world, pos))
			{
				int prio = m_ecoComponent.GetConsumablesPriority(pos, m_cachedState);
				double newDist = blockPos.getSquaredDistance(pos);

				if (m_checkPriority && prio < currentPriority)
				{
					currentPriority = prio;
					posResult = pos.toImmutable();
					if (prio < 1)
						break;
				}
				else if (newDist < currentDistance)
				{
					currentDistance = newDist;
					posResult = pos.toImmutable();
					if (currentDistance < 1.0)
						break;
				}
			}
		}
		if (posResult != null)
		{
			this.targetPos = posResult;
			m_cachedState = this.mob.world.getBlockState(posResult);
			return true;
		}
		return false;
	}

	@Override
	protected boolean isTargetPos(WorldView world, BlockPos pos)
	{
		return IsTargetPosConsumable(world, pos, m_cachedState);
	}

	private void Success()
	{
		OnConsumedSuccess(this.mob.world, this.targetPos, m_cachedState);
		m_shouldStop = true;
	}

}
