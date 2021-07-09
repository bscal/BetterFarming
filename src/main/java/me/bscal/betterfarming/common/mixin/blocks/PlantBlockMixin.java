package me.bscal.betterfarming.common.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlantBlock.class)
public abstract class PlantBlockMixin extends BlockMixin
{

	public PlantBlockMixin(Settings settings)
	{
		super(settings);
	}

}
