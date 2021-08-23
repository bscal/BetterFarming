package me.bscal.betterfarming.common.loot.lootapi;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class LootItem<T>
{
	protected T m_item;

	protected int m_itemCount;
	private List<Predicate<LootContext>> m_preRollChecks;
	private List<BiConsumer<LootContext, ItemStack>> m_postRollModifiers;

	public LootItem(T item)
	{
		this.m_item = item;
	}

	public T GetItem()
	{
		return m_item;
	}

	protected void SetPostRollModifiers(List<BiConsumer<LootContext, ItemStack>> postRollModifiers)
	{
		this.m_postRollModifiers = postRollModifiers;
	}

	public List<BiConsumer<LootContext, ItemStack>> GetPostRollConsumers()
	{
		return m_postRollModifiers == null ? m_postRollModifiers = new ArrayList<>(4) : m_postRollModifiers;
	}

	public void AddPreRollPredicate(BiConsumer<LootContext, ItemStack> consumer)
	{
		GetPostRollConsumers().add(consumer);
	}

	public void AcceptPostRollConsumers(LootContext context, ItemStack currentMutableStack)
	{
		if (m_postRollModifiers != null)
			m_postRollModifiers.forEach(consumer -> consumer.accept(context, currentMutableStack));
	}

	protected void SetPreRollPredicate(List<Predicate<LootContext>> m_preRollChecks)
	{
		this.m_preRollChecks = m_preRollChecks;
	}

	public List<Predicate<LootContext>> GetPreRollPredicates()
	{
		return m_preRollChecks == null ? m_preRollChecks = new ArrayList<>(2) : m_preRollChecks;
	}

	public void AddPreRollPredicate(Predicate<LootContext> predicate)
	{
		GetPreRollPredicates().add(predicate);
	}

	public boolean TestPreRollPredicates(LootContext context)
	{
		if (m_preRollChecks == null)
			return true;

		for (var predicate : m_preRollChecks)
		{
			if (!predicate.test(context))
				return false;
		}
		return true;

	}

	// ***********************************************************
	// Static creation methods

	public static LootItemStack Of(ItemStack stack)
	{
		return new LootItemStack(stack);
	}

	public static LootItemStack Of(ItemStack stack, List<BiConsumer<LootContext, ItemStack>> consumers)
	{
		LootItemStack lootStack = new LootItemStack(stack);
		lootStack.SetPostRollModifiers(consumers);
		return lootStack;
	}

	// ***********************************************************
	// LootItemStack

	public static class LootItemStack extends LootItem<ItemStack>
	{
		public LootItemStack(ItemStack item)
		{
			super(item);
		}

		@Override
		public ItemStack GetItem()
		{
			return m_item.copy();
		}

		public int GetBaseAmount()
		{
			return m_item.getCount();
		}

		public static void AddAmount(ItemStack itemStack, int amount)
		{
			itemStack.setCount(itemStack.getCount() + amount);
		}

		public static void MultiplyAmount(ItemStack itemStack, float amount)
		{
			int count = itemStack.getCount();
			itemStack.setCount((int) (count + Math.floor(count * amount)));
		}
	}

	// ***********************************************************
	// LootLootTable

	public static class LootLootTable extends LootItem<LootTable>
	{
		public LootLootTable(LootTable item)
		{
			super(item);
		}
	}

}
