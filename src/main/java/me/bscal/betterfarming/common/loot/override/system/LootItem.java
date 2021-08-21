package me.bscal.betterfarming.common.loot.override.system;

import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public abstract class LootItem<T>
{

	protected T m_item;

	public LootItem(T item)
	{
		this.m_item = item;
	}

	public T GetItem()
	{
		return m_item;
	}

	public abstract boolean IsLootTable();

	public List<LootDrop> RollNestedTable(LootContext context)
	{
		return Collections.emptyList();
	}

	public static class LootItemStack extends LootItem<ItemStack>
	{
		public LootItemStack(ItemStack item)
		{
			super(item);
		}

		@Override
		public boolean IsLootTable()
		{
			return false;
		}
	}

	public static class LootLootTable extends LootItem<LootTable>
	{
		public LootLootTable(LootTable item)
		{
			super(item);
		}

		@Override
		public boolean IsLootTable()
		{
			return true;
		}

		@Override
		public List<LootDrop> RollNestedTable(LootContext context)
		{
			return m_item.Roll(context, 0);
		}
	}

}
