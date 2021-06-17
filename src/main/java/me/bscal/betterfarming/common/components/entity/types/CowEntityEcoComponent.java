package me.bscal.betterfarming.common.components.entity.types;

import me.bscal.betterfarming.common.utils.BFConstants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class CowEntityEcoComponent extends AnimalEcoComponent
{
	public CowEntityEcoComponent(LivingEntity entity)
	{
		super(entity);
	}

	@Override
	public boolean GetEatableBlocks(WorldView world, BlockPos pos, BlockState state)
	{
		return state.isIn(BFConstants.FARM_FOOD);
	}
}
