package me.bscal.betterfarming.mixin.common.loot;

import me.bscal.betterfarming.common.loot.override.LootOverrideManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(AbstractBlock.class) public class AbstractBlockMixin
{

	@Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;",
			at = @At(value = "HEAD"), cancellable = true)
	public void OnGetDroppedStacks(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir)
	{
		Identifier id = Registry.BLOCK.getId(state.getBlock());
		BlockPos origin = new BlockPos(builder.get(LootContextParameters.ORIGIN));
		Optional<BlockEntity> blockEntiy = Optional.ofNullable(builder.getNullable(LootContextParameters.BLOCK_ENTITY));
		Optional<ItemStack> itemstack = Optional.ofNullable(builder.getNullable(LootContextParameters.TOOL));
		Optional<Entity> entity = Optional.ofNullable(builder.getNullable(LootContextParameters.THIS_ENTITY));

		LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);

		List<ItemStack> loot = LootOverrideManager.Get().RunLootableBlock(id, state, lootContext, origin, blockEntiy, itemstack, entity);

		if (loot != null)
		{
			// Overrides default getDroppedStacks and returns the new loot.
			cir.setReturnValue(loot);
		}
		// Will continue and use default loot generation
	}

}
