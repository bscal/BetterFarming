package me.bscal.betterfarming.common.loot.lootapi;

public abstract class LootContext
{

	public LootTable table;

	public LootContext(LootTable table)
	{
		this.table = table;
	}

}
