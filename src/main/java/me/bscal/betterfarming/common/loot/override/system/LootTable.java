package me.bscal.betterfarming.common.loot.override.system;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class LootTable
{

	public final String name;
	public final boolean alwaysOverrideDefault;
	public final int rolls;

	private final List<LootDrop> m_drops;
	private final Set<LootDrop> m_uniqueDrops;
	private final Random m_random;
	private final List<Predicate<LootContext>> m_preRollChecks;
	// TODO possible move this to be done if Item was chosen during Roll() function
	private final List<BiConsumer<LootContext, List<LootDrop>>> m_postRollModifiers;

	public LootTable(String name, boolean alwaysOverrideDefault, int rolls, List<LootDrop> drops)
	{
		this.name = name;
		this.alwaysOverrideDefault = alwaysOverrideDefault;
		this.rolls = rolls;
		this.m_drops = drops;
		this.m_uniqueDrops = new HashSet<>(drops.size());
		this.m_random = new Random();
		this.m_preRollChecks = new ArrayList<>(4);
		this.m_postRollModifiers = new ArrayList<>(4);
	}

	public void AddPreRollCheck(Predicate<LootContext> preRollCheck)
	{
		this.m_preRollChecks.add(preRollCheck);
	}

	public void AddPostRollCheck(BiConsumer<LootContext, List<LootDrop>> postRollModifier)
	{
		this.m_postRollModifiers.add(postRollModifier);
	}

	public boolean OnPreRoll(LootContext context)
	{
		for (var predicate : m_preRollChecks)
		{
			if (!predicate.test(context))
				return false;
		}
		return true;
	}

	public void OnPostRoll(LootContext context, List<LootDrop> drops)
	{
		m_postRollModifiers.forEach(consumer -> consumer.accept(context, drops));
	}

	public List<LootDrop> Roll(LootContext context, int bonusRolls)
	{
		List<LootDrop> lootResults = new ArrayList<>(8);
		List<LootDrop> rollableLoot = new ArrayList<>(8);

		m_uniqueDrops.clear();

		float totalProbability = 0;
		for (LootDrop drop : m_drops)
		{
			if (drop.isEnabled())
			{
				if (drop.alwaysDrop())
					AddDropToLootResult(lootResults, drop, context);
				else
				{
					totalProbability += drop.chance();
					rollableLoot.add(drop);
				}
			}
		}

		float hitValue = m_random.nextFloat() * totalProbability;
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

		return lootResults;
	}

	private void AddDropToLootResult(List<LootDrop> results, LootDrop drop, LootContext context)
	{
		if (!drop.unique() || !m_uniqueDrops.contains(drop))
		{
			if (drop.unique())
				m_uniqueDrops.add(drop);

			if (drop.item().IsLootTable())
				results.addAll(drop.item().RollNestedTable(context));
			else
				results.add(drop);
		}

	}

}
