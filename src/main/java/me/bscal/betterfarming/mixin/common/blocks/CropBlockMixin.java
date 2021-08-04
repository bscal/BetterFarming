package me.bscal.betterfarming.mixin.common.blocks;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.database.blockdata.BlockData;
import me.bscal.betterfarming.common.database.blockdata.BlockDataManager;
import me.bscal.betterfarming.common.seasons.SeasonalCrop;
import me.bscal.betterfarming.common.seasons.Seasons;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(CropBlock.class) public abstract class CropBlockMixin
{

	@Shadow
	public abstract boolean isMature(BlockState state);

	@Inject(method = "applyGrowth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState" + "(Lnet/minecraft/util"
			+ "/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	public void OnApplyGrowth(World world, BlockPos pos, BlockState state, CallbackInfo ci, int i)
	{
		if (ShouldContinue(state, (ServerWorld) world, pos, i))
		{
			ci.cancel();
		}
	}

	//	@Inject(method = "randomTick", at = @At(value = "HEAD"), cancellable = true)
	//	public void OnRandomTickHead(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
	//	{
	//	}

	@Inject(method = "randomTick", at = @At(value = "INVOKE", target =
			"Lnet/minecraft/server/world/ServerWorld;setBlockState" + "(Lnet" + "/minecraft/util/math/BlockPos;" + "Lnet/minecraft/block" + "/BlockState;I)Z"), cancellable = true)
	public void OnRandomTickInvoke(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
	{
		if (ShouldContinue(state, world, pos, 1))
		{
			ci.cancel();
		}
	}

	private boolean ShouldContinue(BlockState state, ServerWorld world, BlockPos pos, int growthAmount)
	{
		if (!world.isClient)
		{
			BetterFarming.LOGGER.info("Trying to grow!");
			Biome biome = world.getBiome(pos);
			SeasonalCrop crop = BetterFarming.CROP_MANAGER.seasonalCrops.get(state.getBlock());
			BlockData blockData = BlockDataManager.GetOrCreate(world).GetOrCreateEntry(pos, 10, state.getBlock());
			int season = Seasons.GetSeasonForBiome(biome, BetterFarming.SEASON_CLOCK.currentSeason);

			// TODO 2 conditions checks, 1 for if can have growth, 1 for if can grow to full?
			if (crop.CheckConditions(state, world, pos, biome, blockData))
			{
				blockData.growthTime -= crop.growthRate[season];
				if (blockData.growthTime < 1 && crop.growthRate[season] > 0f)
				{
					blockData.growthTime = 10;
					blockData.age += growthAmount;
					BetterFarming.LOGGER.info("Grew canceling!");
					return false;
				}
			}
		}
		return true;
	}
}
