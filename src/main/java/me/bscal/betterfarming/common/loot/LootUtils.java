package me.bscal.betterfarming.common.loot;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public final class LootUtils
{

	public static boolean Chance(float chance)
	{
		return BetterFarming.RAND.nextFloat() > chance;
	}

	public static int RollForBonus(int min, int max, int rolls, float chance)
	{
		int bonus = min;
		while (rolls-- > 0)
		{
			while (bonus < max && Chance(chance))
				bonus++;
		}
		return bonus;
	}

	public static ItemStack RandomItem(Item... items)
	{
		return new ItemStack(items[BetterFarming.RAND.nextInt(items.length - 1)]);
	}

	public static List<ItemStack> RollForItems(int rolls, Item... items)
	{
		List<ItemStack> result = new ArrayList<>();
		while (rolls-- > 0)
			result.add(RandomItem(items));
		return result;
	}

	public static boolean IsWieldingItem(LivingEntity entity, Tag<Item> tag, Hand hand, boolean bothHands)
	{
		return (hand == Hand.MAIN_HAND || bothHands) && entity.getMainHandStack()
				.isIn(tag) || (hand == Hand.OFF_HAND || bothHands) && entity.getOffHandStack().isIn(tag);
	}

	public static boolean HasItem(LivingEntity entity, Tag<Item> tag)
	{
		return entity instanceof PlayerEntity && ((PlayerEntity) entity).getInventory()
				.containsAny(new HashSet<>(tag.values()));
	}

	public static boolean OnCondition(LootData data, Predicate<LootData> condition)
	{
		return condition.test(data);
	}

	public static List<ItemStack> RunEntry(LootData data, ILootDrops drops, boolean dropItems)
	{
		return (dropItems) ? DropListAtEntity(data.entity, drops.Roll(data)) : drops.Roll(data);
	}

	public static List<ItemStack> DropListAtEntity(Entity entity, List<ItemStack> items)
	{
		items.forEach(entity::dropStack);
		return items;
	}

	public static boolean TryCreateItemStack(List<ItemStack> list, Item item, int amount)
	{
		amount = Math.max(0, amount);
		if (amount > 0)
			return list.add(new ItemStack(item, amount));
		return false;
	}

}
