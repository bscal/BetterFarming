package me.bscal.betterfarming.common.loot.lootapi;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class LootTable
{

	public final String name;
	public final boolean alwaysOverrideDefault;
	public final int rolls;

	private final List<LootDrop> m_drops;
	private final Set<LootDrop> m_uniqueDrops;
	private final Random m_random;

	public LootTable(String name, boolean alwaysOverrideDefault, int rolls, List<LootDrop> drops)
	{
		this.name = name;
		this.alwaysOverrideDefault = alwaysOverrideDefault;
		this.rolls = rolls;
		this.m_drops = drops;
		this.m_uniqueDrops = new HashSet<>(drops.size());
		this.m_random = new Random();
	}

	public List<LootDrop> Roll(LootContext context, int bonusRolls)
	{
		List<LootDrop> lootResults = new ArrayList<>();
		List<LootDrop> rollableLoot = new ArrayList<>();

		m_uniqueDrops.clear();

		float totalProbability = 0;
		for (LootDrop drop : m_drops)
		{
			if (drop.isEnabled())
			{
				if (!drop.item().TestPreRollPredicates(context))
					continue;

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

			if (drop.item() instanceof LootItem.LootLootTable table)
				results.addAll(table.GetItem().Roll(context, 0));
			else
				results.add(drop);
		}
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

}
