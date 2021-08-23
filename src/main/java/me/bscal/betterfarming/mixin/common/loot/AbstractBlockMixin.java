package me.bscal.betterfarming.mixin.common.loot;

import me.bscal.betterfarming.common.loot.lootapi.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(AbstractBlock.class) public abstract class AbstractBlockMixin
{

	@Shadow @Nullable protected Identifier lootTableId;

	@Shadow
	public abstract Identifier getLootTableId();

	@Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;",
			at = @At(value = "HEAD"), cancellable = true)
	public void OnGetDroppedStacks(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir)
	{
		if (this.lootTableId != LootTables.EMPTY)
		{
			LootTable lootTable = LootRegistry.GetLootTable(this.lootTableId);
			if (lootTable != null)
			{
				LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
				ServerWorld serverWorld = lootContext.getWorld();
				net.minecraft.loot.LootTable minecraftLootTable = serverWorld.getServer().getLootManager().getTable(this.lootTableId);
				BlockPos origin = new BlockPos(builder.get(LootContextParameters.ORIGIN));

				BlockLootContext blockContext = new BlockLootContext(state, serverWorld, lootContext, minecraftLootTable, origin,
						lootTable);

				Optional<BlockEntity> blockEntity = Optional.ofNullable(builder.getNullable(LootContextParameters.BLOCK_ENTITY));
				blockEntity.ifPresent(be -> blockContext.blockEntity = be);
				Optional<ItemStack> itemstack = Optional.ofNullable(builder.getNullable(LootContextParameters.TOOL));
				itemstack.ifPresent(is -> blockContext.tool = is);
				Optional<Entity> entity = Optional.ofNullable(builder.getNullable(LootContextParameters.THIS_ENTITY));
				entity.ifPresent(e -> blockContext.entity = e);

				List<LootDrop> drops = lootTable.Roll(blockContext, 0);
				if (drops == null || drops.isEmpty())
				{
					// Returns an empty list because we want to override loot
					if (lootTable.alwaysOverrideDefault)
						cir.setReturnValue(Collections.emptyList());
				}
				else
				{
					LootTable.ProcessItemDrop(drops, blockContext);
					// Returns an empty list because we want to override loot
					cir.setReturnValue(Collections.emptyList());
				}
			}
		}
		// Will continue and use default loot generation
	}

}
