package me.bscal.betterfarming.mixin.common.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock
{

	public BlockMixin(Settings settings)
	{
		super(settings);
	}

	@Shadow protected abstract void appendProperties(StateManager.Builder<Block, BlockState> builder);

	@Inject(method = "appendProperties", at = @At(value = "HEAD"))
	protected void OnAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
	{
	}

}
