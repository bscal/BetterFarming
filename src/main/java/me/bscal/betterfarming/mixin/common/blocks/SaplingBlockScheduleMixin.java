package me.bscal.betterfarming.mixin.common.blocks;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.database.blockdata.CropDataBlockHandler;
import me.bscal.betterfarming.common.database.blockdata.blocks.CropDataBlock;
import me.bscal.betterfarming.common.seasons.SeasonalCrop;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SaplingBlock.class) public abstract class SaplingBlockScheduleMixin extends PlantBlockMixin
{

	@Shadow
	public abstract void generate(ServerWorld world, BlockPos pos, BlockState state, Random random);

	protected SaplingBlockScheduleMixin(Settings settings)
	{
		super(settings);
	}

	@Override
	public void OnPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci)
	{
		if (!world.isClient)
		{
			CropDataBlockHandler.GetManager().GetWorld((ServerWorld) world).Create((ServerWorld) world, pos, () -> {
				return new CropDataBlock((SaplingBlock) (Object) this);
			});
		}
	}

	@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
	public void OnRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
	{
		var blockData = CropDataBlockHandler.GetManager().GetBlockData(world, pos);
		if (blockData instanceof CropDataBlock cropDataBlock)
		{
			if (!state.isOf(((CropDataBlock) blockData).block))
				CropDataBlockHandler.GetManager().RemoveBlockData(world, pos);

			BetterFarming.Log("SaplingTick = " + cropDataBlock.totalGrowthReceived + ", age = " + cropDataBlock.age);

			CropDataBlockHandler.TickSapling(cropDataBlock);

			if (cropDataBlock.age > 6)
			{
				CropDataBlockHandler.GetManager().RemoveBlockData(world, pos);
				this.generate(world, pos, state, random);
			}
			ci.cancel();
		}
	}
}
