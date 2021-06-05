package me.bscal.betterfarming.common.loot;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.loot.conditions.CowDrops;
import net.minecraft.util.Identifier;

public class LootRegister
{

	public static final LootTable COW_TABLE = new LootTable(new Identifier(BetterFarming.MOD_ID, "cow_loot"));
	public static final LootTable SHEEP_TABLE = new LootTable(
			new Identifier(BetterFarming.MOD_ID, "sheep_loot"));

	static
	{
		COW_TABLE.Add(new CowDrops());
	}

}
