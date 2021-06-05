package me.bscal.betterfarming.common.loot;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootDrops implements ILootDrops
{

	public final int rolls;
	public int currentRolls;
	public int sum;
	public final List<Integer> weights = new ArrayList<>();
	public final List<LootEntry> entries = new ArrayList<>();

	public LootDrops(int rolls)
	{
		this.rolls = rolls;
	}

	public LootDrops AddEntry(LootEntry entry)
	{
		sum += entry.chance;
		weights.add(entry.chance);
		entries.add(entry);
		return this;
	}

	public LootDrops RemoveEntry(LootEntry entry)
	{
		sum -= entry.chance;
		weights.remove(entry.chance);
		entries.remove(entry);
		return this;
	}

	public List<ItemStack> Roll(LootData data)
	{
		currentRolls = rolls;

		List<ItemStack> result = new ArrayList<>();

		while (currentRolls-- > 0)
		{
			if (data.bonusRolls > 0)
			{
				currentRolls += data.bonusRolls;
				data.bonusRolls = 0;
			}

			int random = BetterFarming.RAND.nextInt(sum);
			int totalWeight = 0;
			for (int i = 0; i < weights.size(); i++)
			{
				totalWeight += weights.get(i);
				if (random < totalWeight)
				{
					LootPool pool = entries.get(i).pool;
					data.CopyItems(pool.items);
					if (pool.condition == null || pool.condition.test(data))
					{
						if (pool.onSuccess != null)
							pool.onSuccess.accept(data);
						result.addAll(data.items);
						break;
					}
				}
			}
		}

		//		while (currentRolls-- > 0)
		//		{
		//			int rand = BetterFarming.RAND.nextInt(sum);
		//			Map.Entry<Integer, LootEntry> mapEntry = map.ceilingEntry(rand);
		//			LootPool pool = mapEntry.getValue().pool;
		//			data.CopyItems(pool.items);
		//			if (pool.condition == null || pool.condition.test(data))
		//			{
		//				if (pool.onSuccess != null)
		//					pool.onSuccess.accept(data);
		//				result.items.addAll(data.items);
		//			}
		//		}
		return result;
	}

	public boolean HasRolls()
	{
		return currentRolls > 0;
	}

}
