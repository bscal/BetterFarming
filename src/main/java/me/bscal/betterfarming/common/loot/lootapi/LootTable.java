package me.bscal.betterfarming.common.loot.lootapi;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class LootTable
{

	public final String name;
	public final boolean alwaysOverrideDefault;
	public final int rolls;

	public boolean canceled;

	private final List<LootDrop> m_drops;
	private final Set<LootDrop> m_uniqueDrops;
	private final Random m_random;
	private final List<Function<LootContext, List<LootDrop>>> m_dynamicModifiers;

	public LootTable(String name, boolean alwaysOverrideDefault, int rolls)
	{
		this(name, alwaysOverrideDefault, rolls, new ArrayList<>(2));
	}

	public LootTable(String name, boolean alwaysOverrideDefault, int rolls, List<LootDrop> drops)
	{
		this.name = name;
		this.alwaysOverrideDefault = alwaysOverrideDefault;
		this.rolls = rolls;
		this.m_drops = drops;
		this.m_uniqueDrops = new HashSet<>(drops.size());
		this.m_random = new Random();
		this.m_dynamicModifiers = new ArrayList<>(2);
	}

	public List<LootDrop> Roll(LootContext context, int bonusRolls)
	{
		List<LootDrop> lootResults = new ArrayList<>();
		List<LootDrop> rollableLoot = new ArrayList<>();

		canceled = false;
		m_uniqueDrops.clear();

		float totalProbability = 0;
		List<LootDrop> constantAndDynamicDrops = new ArrayList<>(m_drops.size() + m_dynamicModifiers.size());
		constantAndDynamicDrops.addAll(m_drops);
		constantAndDynamicDrops.addAll(ProcessDynamicDrops(context));

		if (canceled)
			return null;

		for (LootDrop drop : constantAndDynamicDrops)
		{
			if (drop.isEnabled())
			{
				if (!drop.item().TestPreRollPredicates(context))
					continue;

				if (drop.alwaysDrop())
				{
					AddDropToLootResult(lootResults, drop, context);
				}
				else
				{
					totalProbability += drop.chance();
					rollableLoot.add(drop);
				}
			}
		}

		float hitValue = m_random.nextFloat() * totalProbability;
		// We want to include always drop items into the roll amount
		int totalRolls = rolls + bonusRolls - lootResults.size();
		for (int i = 0; i < totalRolls; i++)
		{
			float rollTotalValue = 0;
			for (LootDrop rollableDrop : rollableLoot)
			{
				rollTotalValue += rollableDrop.chance();
				if (hitValue < rollTotalValue)
				{
					AddDropToLootResult(lootResults, rollableDrop, context);
					break;
				}
			}
		}

		// Null is checked and if alwaysOverrideDefaults is false will use minecraft implementation for drops, if true no drops.
		if (canceled)
			return null;

		return lootResults;
	}

	private void AddDropToLootResult(List<LootDrop> results, LootDrop drop, LootContext context)
	{
		if (!drop.unique() || !m_uniqueDrops.contains(drop))
		{
			if (drop.unique())
				m_uniqueDrops.add(drop);

			if (drop.item() instanceof LootItem.LootLootTable table)
				results.addAll(table.GetItem().Roll(context, 0));
			else
				results.add(drop);
		}
	}

	public void AddDynamicDrops(Function<LootContext, List<LootDrop>> dynamicModifier)
	{
		m_dynamicModifiers.add(dynamicModifier);
	}

	public List<LootDrop> ProcessDynamicDrops(LootContext context)
	{
		List<LootDrop> results = new ArrayList<>();
		m_dynamicModifiers.forEach(func -> results.addAll(func.apply(context)));
		return results;
	}

	public static void ProcessItemDrop(List<LootDrop> drops, LootContext context)
	{
		for (LootDrop drop : drops)
		{
			if (drop.item() instanceof LootItem.LootItemStack item)
			{
				ItemStack stack = item.GetItem();
				drop.item().AcceptPostRollConsumers(context, stack);
				if (context instanceof EntityLootContext entContext)
					entContext.entity.dropStack(stack);
				else if (context instanceof BlockLootContext blockContext)
					Block.dropStack(blockContext.world, blockContext.origin, stack);
			}

		}
	}

	public static List<LootDrop> ItemStackToLootDrop(List<ItemStack> itemStacks)
	{
		List<LootDrop> res = new ArrayList<>(itemStacks.size());
		itemStacks.forEach(itemStack -> res.add(LootDrop.Of(itemStack)));
		return res;
	}

	public void AddDrop(LootDrop lootDrop)
	{
		m_drops.add(lootDrop);
	}
}
