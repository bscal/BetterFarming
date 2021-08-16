package me.bscal.betterfarming.common.loot.override;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class LootOverrideManager
{

	private static LootOverrideManager m_INSTANCE;

	private Object2ObjectOpenHashMap<Identifier, BlockLootable> m_blockLoot;
	private Object2ObjectOpenHashMap<Identifier, EntityLootable> m_entityLoot;

	public List<ItemStack> RunLootableBlock(Identifier blockId, BlockState state, LootContext context,
			BlockPos origin, Optional<BlockEntity> blockEntity, Optional<ItemStack> itemStack, Optional<Entity> enity)
	{
		return m_blockLoot.get(blockId).Generate(state, context, context.getWorld(), origin, blockEntity, itemStack, enity);
	}

	public List<ItemStack> RunLootableEntity(Identifier entityId, LivingEntity sourceEntity, LootContext context, DamageSource source, boolean causedByPlayer,
			Identifier lootId, LootTable lootTable)
	{
		return m_entityLoot.get(entityId).Generate(sourceEntity, context, source, causedByPlayer, lootId, lootTable, context.getWorld());
	}

	public void DropEntityLoot(List<ItemStack> stack, LivingEntity sourceEntity, float yOffset)
	{
		for (ItemStack itemStack : stack)
		{
			sourceEntity.dropStack(itemStack, yOffset);
		}
	}

	public void DropBlockLoot(List<ItemStack> stack, ServerWorld world, BlockPos pos, @Nullable Direction dir)
	{
		for (ItemStack itemStack : stack)
		{
			if (dir != null)
				Block.dropStack(world, pos, dir, itemStack);
			else
				Block.dropStack(world, pos, itemStack);
		}
	}

	public static LootOverrideManager Get()
	{
		return m_INSTANCE == null ? m_INSTANCE = new LootOverrideManager() : m_INSTANCE;
	}

}
