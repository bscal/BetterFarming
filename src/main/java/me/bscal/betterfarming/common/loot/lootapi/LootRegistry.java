package me.bscal.betterfarming.common.loot.lootapi;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public final class LootRegistry
{

	private static final Object2ObjectOpenHashMap<Identifier, LootTable> m_idToLootTable = new Object2ObjectOpenHashMap<>();


	public static void Register(Identifier id, LootTable table)
	{
		m_idToLootTable.put(id, table);
	}

	public static LootTable GetLootTable(Identifier id)
	{
		return m_idToLootTable.get(id);
	}

	static
	{
		List<LootDrop> wheatDrops = List.of(new LootDrop(1.0f, false, false, true, LootItem.Of(new ItemStack(Items.APPLE, 1))));
		LootTable wheatTable = new LootTable("wheat", false, 1, wheatDrops);
		Register(Blocks.WHEAT.getLootTableId(), wheatTable);
	}

}
