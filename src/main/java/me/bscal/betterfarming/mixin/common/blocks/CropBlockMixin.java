package me.bscal.betterfarming.mixin.common.blocks;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.database.blockdata.CropDataBlockHandler;
import me.bscal.betterfarming.common.database.blockdata.blocks.CropDataBlock;
import me.bscal.betterfarming.common.seasons.SeasonalCrop;
import me.bscal.betterfarming.common.seasons.Seasons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
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

@Mixin(CropBlock.class) public abstract class CropBlockMixin extends PlantBlockMixin implements Fertilizable
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

	@Override
	public void OnPlacedInject(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci)
	{
		SeasonalCrop crop = BetterFarming.CROP_MANAGER.Get(state.getBlock());
		if (crop != null)
		{
			int age = state.get(CropBlock.AGE);
			CropDataBlockHandler.GetManager().Create((ServerWorld) world, pos, () -> {
				if (age > 0)
					new CropDataBlock((Block) (Object) this);
				return new CropDataBlock((Block) (Object) this, crop.GetGrowthFromAge(age), 0, age);
			});
		}
	}

	@Inject(method = "applyGrowth", at = @At(value = "HEAD"), cancellable = true)
	public void OnApplyGrowth(World world, BlockPos pos, BlockState state, CallbackInfo ci)
	{
		int i = this.getAge(state) + getGrowthAmount(world);
		int j = this.getMaxAge();
		if (i > j)
		{
			i = j;
		}
		if (i < getMaxAge())
		{
			SeasonalCrop crop = BetterFarming.CROP_MANAGER.Get(state.getBlock());
			if (crop != null)
			{
				float f = getAvailableMoisture((Block) (Object) this, world, pos);
				// f is weird... 1.0 is minimum the main (this) moist farmland block can be, I did not calculate other outcomes.
				if (f >= 1.0f && CanGrow(state, (ServerWorld) world, pos, crop, i))
				{
					world.setBlockState(pos, withAge(i + 1), Block.NOTIFY_LISTENERS);
				}
				ci.cancel();
			}
		}
	}

	@Inject(method = "randomTick", at = @At(value = "INVOKE", target =
			"Lnet/minecraft/block/CropBlock;getAge" + "(Lnet/minecraft/block" + "/BlockState;)I"), cancellable = true)
	public void OnRandomTickInvoke(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
	{
		int i = getAge(state);
		if (i < getMaxAge())
		{
			SeasonalCrop crop = BetterFarming.CROP_MANAGER.Get(state.getBlock());
			if (crop != null)
			{
				float f = getAvailableMoisture((Block) (Object) this, world, pos);
				// f is weird... 1.0 is minimum the main (this) moist farmland block can be, I did not calculate other outcomes.
				if (f >= 1.0f && CanGrow(state, world, pos, crop, 1))
				{
					world.setBlockState(pos, withAge(i + 1), Block.NOTIFY_LISTENERS);
				}
				ci.cancel();
			}
		}
	}

	private boolean CanGrow(BlockState state, ServerWorld world, BlockPos pos, SeasonalCrop crop, int growthAmount)
	{
		if (!world.isClient)
		{
			Biome biome = world.getBiome(pos);
			CropDataBlock blockData = (CropDataBlock) CropDataBlockHandler.GetManager().GetBlockData(world, pos);
			if (blockData == null)
				return false;

			int season = Seasons.GetSeasonForBiome(biome, BetterFarming.SEASON_CLOCK.currentSeason);

			if (crop.ShouldRemove(state, world, pos, biome, blockData, season))
			{
				CropDataBlockHandler.GetManager().RemoveBlockData(world, pos);
				BetterFarming.LOGGER.info("Removed!");
				return false;
			}

			blockData.ableToGrow = crop.TestGrowingCondition(state, world, pos, biome, blockData, season);
			if (blockData.ableToGrow && crop.TickGrowth(state, world, pos, biome, blockData, season))
			{
				crop.OnGrow(state, world, pos, blockData, growthAmount);
				CropDataBlockHandler.GetManager().RemoveBlockData(world, pos);
				BetterFarming.LOGGER.info("Grew!");
				return true;
			}
		}
		return false;
	}
}
