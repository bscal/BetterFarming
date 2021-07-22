package me.bscal.betterfarming.mixin.common.blocks;

import net.minecraft.block.PlantBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlantBlock.class)
public abstract class PlantBlockMixin extends BlockMixin
{

	public PlantBlockMixin(Settings settings)
	{
		super(settings);
	}

}
