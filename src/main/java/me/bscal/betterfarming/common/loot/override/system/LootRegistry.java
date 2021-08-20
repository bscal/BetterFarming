package me.bscal.betterfarming.common.loot.override.system;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

public class LootRegistry
{

	private final Object2ObjectOpenHashMap<Identifier, LootTable> m_idToLootTable;

	public LootRegistry()
	{
		m_idToLootTable = new Object2ObjectOpenHashMap<>();
	}

	public void Register(Identifier id, LootTable table)
	{
		m_idToLootTable.put(id, table);
	}

	public LootTable GetLootTable(Identifier id)
	{
		return m_idToLootTable.get(id);
	}

}
