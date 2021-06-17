package me.bscal.betterfarming.common.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SpreadableBlock.class) public abstract class SpreadableBlockMixin extends SnowyBlock
{

	private static final IntProperty SPREAD_AMOUNT = IntProperty.of("spread", 0, 255);

	protected SpreadableBlockMixin(Settings settings)
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

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "appendProperties(Lnet/minecraft/state/StateManager$Builder;)V", at = @At(value = "HEAD"))
	protected void OnAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
	{
		builder.add(SPREAD_AMOUNT);
	}

	// Prolongs grass spreading.
	@Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/SpreadableBlock;getDefaultState()Lnet/minecraft/block/BlockState;"), cancellable = true)
	public void OnRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random,
			CallbackInfo ci)
	{
		int spread = state.get(SPREAD_AMOUNT);
		// TODO configurable amount
		if (spread < 7)
		{
			world.setBlockState(pos, state.with(SPREAD_AMOUNT, spread + 1));
			ci.cancel();
		}
	}
}
