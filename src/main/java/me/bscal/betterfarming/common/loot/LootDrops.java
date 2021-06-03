package me.bscal.betterfarming.common.loot;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LootDrops
{

	public int rolls;
	public int currentRolls;
	public int sum;
	public TreeMap<Integer, LootEntry> map = new TreeMap<>();

	public LootDrops(int rolls)
	{
		this.rolls = rolls;
	}

	public void AddItem(LootEntry entry)
	{
		sum += entry.chance;
		map.put(entry.chance, entry);
	}

	public void Remove(LootEntry entry)
	{
		sum -= entry.chance;
		map.remove(entry.chance);
	}

	public LootDropResult Roll(LootData data)
	{
		currentRolls = rolls;

		LootDropResult result = new LootDropResult();
		while (currentRolls-- > 0)
		{
			int rand = BetterFarming.RAND.nextInt(sum);
			Map.Entry<Integer, LootEntry> mapEntry = map.floorEntry(rand);
			LootPool pool = mapEntry.getValue().pool;
			if (pool.condition == null || pool.condition.test(data))
			{
				if (pool.onSuccess != null)
					pool.onSuccess.accept(data);
				result.items.addAll(pool.item);
			}
		}
		return result;
	}

	public boolean HasRolls()
	{
		return currentRolls > 0;
	}

	public static class LootEntry
	{
		final int chance;
		final LootPool pool;

		public LootEntry(int chance, ItemStack item)
		{
			this.chance = chance;
			this.pool = new LootPool(item, null, null);
		}

		public LootEntry(int chance, LootPool lootPool)
		{
			this.chance = chance;
			this.pool = lootPool;
		}
	}

	public static class LootPool
	{
		final List<ItemStack> item;
		final Predicate<LootData> condition;
		final Consumer<LootData> onSuccess;

		public LootPool(@NotNull ItemStack item, @Nullable Predicate<LootData> condition,
				@Nullable Consumer<LootData> onSuccess)
		{
			this.item = new ArrayList<>(){{add(item);}};
			this.condition = condition;
			this.onSuccess = onSuccess;
		}

		public LootPool(@NotNull List<ItemStack> item, @Nullable Predicate<LootData> condition,
				@Nullable Consumer<LootData> onSuccess)
		{
			this.item = item;
			this.condition = condition;
			this.onSuccess = onSuccess;
		}
	}

	public static class LootData
	{
		public LivingEntity entity;
		public LivingEntity attacker;
		public ServerWorld world;
	}

	public static class LootDropResult
	{
		public List<ItemStack> items = new ArrayList<>();
	}

}
