package me.bscal.betterfarming.common.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scheduler.Scheduleable;

@Mixin(SaplingBlock.class) public abstract class SaplingBlockMixin extends BlockMixin
		implements Scheduleable
{

	private static final BooleanProperty STARTED;

	public SaplingBlockMixin(Settings settings)
	{
		super(settings);
	}

	// Should allow us to inject into super.appendProperties without actually overriding it
	@Override
	protected void OnAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
	{
		builder.add(STARTED);
	}

	static
	{
		STARTED = BooleanProperty.of("started");
	}

}
