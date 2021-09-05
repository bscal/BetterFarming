package me.bscal.betterfarming.mixin.common.blocks;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.schedulers.FastDelayScheduler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SaplingBlock.class) public abstract class SaplingBlockScheduleMixin extends PlantBlock
{

	@Shadow
	public abstract void generate(ServerWorld world, BlockPos pos, BlockState state, Random random);

	private static final BooleanProperty STARTED;
	private static final int MIN_COUNT_FOR_GROWTH;
	private static final int MAX_COUNT_FOR_GROWTH;
	private static final float GROWTH_CHANCE = .2f;

	protected SaplingBlockScheduleMixin(Settings settings)
	{
		super(settings);
	}

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void OnConstructor(SaplingGenerator generator, Settings settings, CallbackInfo ci)
	{
		this.setDefaultState(this.getDefaultState().with(STARTED, false));
	}

	@Inject(method = "appendProperties", at = @At(value = "HEAD"))
	protected void OnAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
	{
		builder.add(STARTED);
	}

	public void onScheduleEnd(World world, BlockPos pos, int scheduleId, NbtCompound additionalData)
	{
		if (!world.isClient())
		{
			int age = (additionalData.contains("age")) ? additionalData.getInt("age") : 0;
			BlockState state = world.getBlockState(pos);
			BetterFarming.LOGGER.info(pos);

			if (!state.contains(STARTED) || !(state.getBlock() instanceof SaplingBlock))
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
			}
		}
	}

	@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
	public void OnRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
	{
		BetterFarming.LOGGER.info("RandomTick = " + state.get(STARTED));
		if (!state.get(STARTED))
		{
			world.setBlockState(pos, state.with(STARTED, true).cycle(Properties.STAGE));

			NbtCompound data = new NbtCompound();
			data.putInt("x", pos.getX());
			data.putInt("y", pos.getY());
			data.putInt("z", pos.getZ());
			data.putString("block", String.valueOf(Registry.BLOCK.getId(state.getBlock())));
			data.putString("world", world.getRegistryKey().getValue().toString());
			FastDelayScheduler.INSTANCE.ScheduleRunnable(100, false, true, data, (entry) ->
			{
				ServerWorld blockWorld = BetterFarming.GetServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(data.getString("world"))));
				if (blockWorld == null)
					return false;
				BlockPos blockPos = new BlockPos(data.getInt("x"), data.getInt("y"), data.getInt("z"));
				BlockState blockState = blockWorld.getBlockState(blockPos);
				Identifier blockId = Identifier.tryParse(entry.additionalData().getString("block"));
				if (blockId == null)
					return false;
				Block block = Registry.BLOCK.get(blockId);

				if (blockState.isOf(block))
				{
					if (blockWorld.random.nextFloat() < .17f);
				}

				return false;
			});

			BetterFarming.LOGGER.info("Tick called.");
		}

		ci.cancel();
	}

	static
	{
		STARTED = BooleanProperty.of("started");
		MIN_COUNT_FOR_GROWTH = 1;//3 * BFConstants.REAL_DAY_TICKS;
		MAX_COUNT_FOR_GROWTH = 2;//MIN_COUNT_FOR_GROWTH + 6 * BFConstants.REAL_HOUR_TICKS;
	}

}
