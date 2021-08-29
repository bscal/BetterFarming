package me.bscal.betterfarming.common.loot.lootapi;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

public record LootDrop(float chance, boolean unique, boolean alwaysDrop, boolean isEnabled, LootItem<?> item)
{

	public LootDrop(LootItem<?> item)
	{
		this(0f, false, true, true, item);
	}

	public LootDrop(float chance, LootItem<?> item)
	{
		this(chance, false, false, true, item);
	}

	public LootDrop Clone()
	{
		return new LootDrop(chance, unique, alwaysDrop, isEnabled, item);
	}

	public static LootDrop Of(ItemStack stack)
	{
		return new LootDrop(LootItem.Of(stack));
	}

	public static LootDrop Of(ItemConvertible item, int amount)
	{
		return new LootDrop(LootItem.Of(new ItemStack(item, amount)));
	}

	public static LootDrop Of(float chance, ItemConvertible item, int amount)
	{
		return new LootDrop(chance, LootItem.Of(new ItemStack(item, amount)));
	}

}
