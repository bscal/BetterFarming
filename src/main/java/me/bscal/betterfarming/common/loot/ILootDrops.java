package me.bscal.betterfarming.common.loot;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface ILootDrops
{

	List<ItemStack> Roll(LootData data);

	default void Drop(LootData data, List<ItemStack> stack)
	{
		stack.forEach(data.entity::dropStack);
	}

}
