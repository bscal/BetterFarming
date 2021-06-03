package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.common.events.LootManagerEarlyAssignCallback;
import me.bscal.betterfarming.common.mixin.LootTableAccessor;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.Items;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

import java.util.Map;

public class LootManagerListener implements LootManagerEarlyAssignCallback, LootTableLoadingCallback
{
	private static final Identifier COW_LOOT_TABLE_ID = new Identifier("minecraft", "entities/cow");

	@Override
	public TypedActionResult<Map<Identifier, LootTable>> OnEarlyApply(Map<Identifier, LootTable> tables,
			ResourceManager manager, LootManager lootManager)
	{
		FabricLootPoolBuilder pool = FabricLootPoolBuilder.builder()
				.rolls(ConstantLootNumberProvider.create(1.0f))
				.withEntry(ItemEntry.builder(Items.EGG).build());

		LootTableAccessor lootTableAccessor = (LootTableAccessor) tables.get(COW_LOOT_TABLE_ID);
		lootTableAccessor.SetPools(new LootPool[] { pool.build() });

		return TypedActionResult.success(tables);
	}

	@Override
	public void onLootTableLoading(ResourceManager resourceManager, LootManager manager, Identifier id,
			FabricLootSupplierBuilder supplier, LootTableSetter setter)
	{
		if (COW_LOOT_TABLE_ID.equals(id))
		{
			Clear(setter);
		}
	}

	private void Clear(LootTableSetter setter)
	{
		setter.set(FabricLootSupplierBuilder.of(
				LootTable.builder().pool(FabricLootPoolBuilder.builder()).build()).build());
	}
}
