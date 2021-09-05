package me.bscal.betterfarming.mixin.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlantBlock.class)
public abstract class PlantBlockMixin extends BlockMixin
{

	public PlantBlockMixin(Settings settings)
	{
		super(settings);
	}

	@Override
	public void OnPlacedInject(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci)
	{
		super.OnPlacedInject(world, pos, state, placer, itemStack, ci);
	}
}
