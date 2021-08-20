package me.bscal.betterfarming.common.loot.override.system;

import java.util.*;

public class LootTable
{

	public final String name;
	public final int rolls;
	private final List<LootDrop> drops;
	private final Set<LootDrop> uniqueDrops;
	private final Random random;

	public LootTable(String name, int rolls, List<LootDrop> drops)
	{
		this.name = name;
		this.rolls = rolls;
		this.drops = drops;
		this.uniqueDrops = new HashSet<>(drops.size());
		this.random = new Random();
	}

	public List<LootDrop> Roll(int bonusRolls)
	{
		List<LootDrop> lootResults = new ArrayList<>(8);
		List<LootDrop> rollableLoot = new ArrayList<>(8);

		uniqueDrops.clear();

		float totalProbability = 0;
		for (LootDrop drop : drops)
		{
			if (drop.isEnabled())
			{
				if (drop.alwaysDrop())
					AddDropToLootResult(lootResults, drop);
				else
				{
					totalProbability += drop.chance();
					rollableLoot.add(drop);
				}
			}
		}

		float hitValue = random.nextFloat() * totalProbability;
		int totalRolls = rolls + bonusRolls - lootResults.size();
		for (int i = 0; i < totalRolls; i++)
		{
			float rollTotalValue = 0;
			for (LootDrop rollableDrop : rollableLoot)
			{
				rollTotalValue += rollableDrop.chance();
				if (hitValue < rollTotalValue)
				{
					AddDropToLootResult(lootResults, rollableDrop);
					break;
				}
			}
		}

		return lootResults;
	}

	private void AddDropToLootResult(List<LootDrop> results, LootDrop drop)
	{

		if (drop.unique())
		{
			if (!uniqueDrops.contains(drop))
			{
				uniqueDrops.add(drop);
				results.add(drop);
			}
			return;
		}
		results.add(drop);
	}

}
