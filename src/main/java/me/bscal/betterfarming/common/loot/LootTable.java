package me.bscal.betterfarming.common.loot;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class LootTable
{

	public final Identifier id;
	public final List<ILootDrops> drops = new ArrayList<>();

	public LootTable(Identifier id)
	{
		this.id = id;
	}

	public LootTable Add(ILootDrops drop)
	{
		drops.add(drop);
		return this;
	}

	public void Roll(LootData data)
	{
		drops.forEach((drop) -> drop.Roll(data));
	}

	public void RollAndDrop(LootData data)
	{
		drops.forEach((drops) -> drops.Drop(data, drops.Roll(data)));
	}
}
