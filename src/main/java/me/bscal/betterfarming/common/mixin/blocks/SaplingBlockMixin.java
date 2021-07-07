package me.bscal.betterfarming.common.mixin.blocks;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.BFConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scheduler.Scheduleable;
import scheduler.Scheduler;

import java.util.Random;

@Mixin(SaplingBlock.class) public abstract class SaplingBlockMixin extends PlantBlock implements Scheduleable
{
	@Shadow
	public abstract void generate(ServerWorld world, BlockPos pos, BlockState state, Random random);

	private static final BooleanProperty STARTED;
	private static final int MIN_COUNT_FOR_GROWTH;
	private static final int MAX_COUNT_FOR_GROWTH;
	private static final float GROWTH_CHANCE = .2f;

	protected SaplingBlockMixin(Settings settings)
	{
		super(settings);
	}

	@Override
	public void onScheduleEnd(World world, BlockPos pos, int scheduleId, NbtCompound additionalData)
	{
		if (!world.isClient())
		{
			int age = (additionalData.contains("age")) ? additionalData.getInt("age") : 0;
			BlockState state = world.getBlockState(pos);

			if (!state.get(STARTED) || !(state.getBlock() instanceof SaplingBlock))
				return;

			boolean success = age >= MAX_COUNT_FOR_GROWTH || (age >= MIN_COUNT_FOR_GROWTH && world.random.nextFloat() < GROWTH_CHANCE);

			BetterFarming.LOGGER.info("OnScheduleEnd :: " + success);

			if (success)
			{
				this.generate((ServerWorld) world, pos, state, world.random);
			}
			else
			{
				additionalData.putInt("age", age + 1);
				Scheduler.Builder(this, world)
						.additionalData(additionalData)
						.schedule(1);
			}
		}
	}

	@Inject(method = "appendProperties(Lnet/minecraft/state/StateManager$Builder;)V", at = @At(value = "HEAD"))
	protected void OnAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
	{
		builder.add(STARTED);
	}

	@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
	public void OnRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random,
			CallbackInfo ci)
	{
		if (!state.get(STARTED))
		{
			state.with(STARTED, true);
			Scheduler.Builder(this, world).schedule(1);
			BetterFarming.LOGGER.info("Tick called.");
		}

		ci.cancel();
	}

	static
	{
		STARTED = Properties.CONDITIONAL;
		MIN_COUNT_FOR_GROWTH = 1;//3 * BFConstants.REAL_DAY_TICKS;
		MAX_COUNT_FOR_GROWTH = 2;//MIN_COUNT_FOR_GROWTH + 6 * BFConstants.REAL_HOUR_TICKS;
	}

}
