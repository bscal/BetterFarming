package me.bscal.betterfarming.common.loot;

import net.minecraft.item.ItemStack;

public class LootEntry
{
	public final int chance;
	public final LootPool pool;

	public LootEntry(int chance, ItemStack item)
	{
		this.chance = chance;
		this.pool = new LootPool(item);
	}

	public LootEntry(int chance, LootPool lootPool)
	{
		this.chance = chance;
		this.pool = lootPool;
	}

}
