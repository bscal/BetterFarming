package me.bscal.betterfarming.common.loot.override.system;

public abstract class LootContext
{

	public LootTable table;

	public LootContext(LootTable table)
	{
		this.table = table;
	}

}
