package me.bscal.betterfarming.common.loot.override;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class BlockLootable
{

	/**
	 * Generate loot for blocks.<br>
	 * - Returning null will skip and use the default loot gen implementations<br>
	 * - Returning the ItemStack List will override the default loot gen and use the returned list.<br>
	 * - Returning an empty List (constant <code>LootOverrideManager.EMPTY_LOOTABLE</code>) will drop nothing.<br>
	 * - You can also choose to use static <code>Block.dropStack</code> functions to drop your items.<br>
	 *<br>
	 * - Loot gen functions are called from a several classes and blocks there are nullable parameters wrapped in optionals.
	 */
	public List<ItemStack> Generate(BlockState state, LootContext context, ServerWorld world, BlockPos origin,
			Optional<BlockEntity> blockEntity, Optional<ItemStack> tool, Optional<Entity> entity)
	{
		return null;
	}

}
