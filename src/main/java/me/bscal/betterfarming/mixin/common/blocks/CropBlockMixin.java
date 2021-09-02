package me.bscal.betterfarming.mixin.common.blocks;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.database.blockdata.BlockData;
import me.bscal.betterfarming.common.seasons.SeasonalCrop;
import me.bscal.betterfarming.common.seasons.Seasons;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CropBlock.class) public abstract class CropBlockMixin extends PlantBlock implements Fertilizable
{

	protected CropBlockMixin(Settings settings)
	{
		super(settings);
	}

	@Shadow
	protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos)
	{
		return 0;
	}

	@Shadow
	protected abstract int getAge(BlockState state);

	@Shadow
	public abstract int getMaxAge();

	@Shadow
	public abstract BlockState withAge(int age);

	@Shadow
	protected abstract int getGrowthAmount(World world);

	@Inject(method = "applyGrowth", at = @At(value = "HEAD"), cancellable = true)
	public void OnApplyGrowth(World world, BlockPos pos, BlockState state, CallbackInfo ci)
	{
		int i = this.getAge(state) + getGrowthAmount(world);
		int j = this.getMaxAge();
		if (i > j)
		{
			i = j;
		}

		if (CanGrow(state, (ServerWorld) world, pos, i))
		{
			world.setBlockState(pos, this.withAge(i), Block.NOTIFY_LISTENERS);
		}
		ci.cancel();
	}

	@Inject(method = "randomTick", at = @At(value = "INVOKE", target =
			"Lnet/minecraft/block/CropBlock;getAge" + "(Lnet/minecraft/block" + "/BlockState;)I"), cancellable = true)
	public void OnRandomTickInvoke(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
	{
		int i = getAge(state);
		if (i < getMaxAge())
		{
			float f = getAvailableMoisture((Block) ((Object) this), world, pos);
			// f is weird... 1.0 is minimum the main (this) moist farmland block can be, I did not calculate other outcomes.
			if (f >= 1.0f && CanGrow(state, world, pos, 1))
			{
				world.setBlockState(pos, withAge(i + 1), Block.NOTIFY_LISTENERS);
			}
		}
		ci.cancel();
	}

	private boolean CanGrow(BlockState state, ServerWorld world, BlockPos pos, int growthAmount)
	{
		if (!world.isClient)
		{
			Biome biome = world.getBiome(pos);
			SeasonalCrop crop = BetterFarming.CROP_MANAGER.seasonalCrops.get(state.getBlock());

			if (crop == null)
				return false;

			BlockData blockData = (BlockData) BetterFarming.WORLD_DATAMANGER.GetOrCreateBlockData(world, pos,
					() -> new BlockData(0, crop.baseGrowthTicks, 0, this));
			int season = Seasons.GetSeasonForBiome(biome, BetterFarming.SEASON_CLOCK.currentSeason);

			if (!state.isOf(blockData.block) || crop.ShouldRemove(state, world, pos, biome, blockData, season))
			{
				BetterFarming.WORLD_DATAMANGER.RemoveBlockData(world, pos);
				BetterFarming.LOGGER.info("Removed!");
				return false;
			}

			blockData.ableToGrow = crop.CheckGrowingCondition(state, world, pos, biome, blockData, season);
			if (blockData.ableToGrow)
			{
				blockData.growthTime -= crop.growthRate[season];

				if (crop.CanFullyGrow(state, world, pos, biome, blockData, season))
				{
					crop.HandleGrowth(state, world, pos, biome, blockData, season, growthAmount);
					BetterFarming.LOGGER.info("Grew!");
					BetterFarming.WORLD_DATAMANGER.RemoveBlockData(world, pos);
					return true;
				}
			}
		}
		return false;
	}
}
