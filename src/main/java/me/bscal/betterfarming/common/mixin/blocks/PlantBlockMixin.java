package me.bscal.betterfarming.common.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlantBlock.class)
public abstract class PlantBlockMixin extends Block
{

	public PlantBlockMixin(Settings settings)
	{
		super(settings);
	}

	// Should allow us to inject into super.appendProperties without actually overriding it
	@Override
	@Intrinsic
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
	}

}
