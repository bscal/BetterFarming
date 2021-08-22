package me.bscal.betterfarming.common.loot.lootapi;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLootContext extends me.bscal.betterfarming.common.loot.lootapi.LootContext
{
	public @NotNull BlockState state;
	public @NotNull Identifier blockId;
	public @NotNull ServerWorld world;
	public @NotNull Block block;
	public @NotNull LootContext minecraftLootContext;
	public @NotNull net.minecraft.loot.LootTable minecraftLootTable;
	public @NotNull BlockPos origin;
	public @Nullable BlockEntity blockEntity;
	public @Nullable ItemStack tool;
	public @Nullable Entity entity;

	public BlockLootContext(@NotNull BlockState state, @NotNull ServerWorld world, @NotNull LootContext minecraftLootContext,
			@NotNull net.minecraft.loot.LootTable minecraftLootTable, @NotNull BlockPos origin, @NotNull LootTable overrideLootTable)
	{
		super(overrideLootTable);
		this.state = state;
		this.block = state.getBlock();
		this.blockId = Registry.BLOCK.getId(this.block);
		this.world = world;
		this.minecraftLootContext = minecraftLootContext;
		this.minecraftLootTable = minecraftLootTable;
		this.origin = origin;
	}
}


