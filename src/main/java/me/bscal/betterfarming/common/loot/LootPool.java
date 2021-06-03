package me.bscal.betterfarming.common.loot;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LootPool
{
	public List<ItemStack> items;
	public Predicate<LootData> condition;
	public Consumer<LootData> onSuccess;

	public LootPool(@NotNull ItemStack... items)
	{
		this.items = Arrays.asList(items);
	}

	public LootPool(@NotNull ItemStack items, @Nullable Predicate<LootData> condition,
			@Nullable Consumer<LootData> onSuccess)
	{
		this.items = new ArrayList<>(1)
		{{
			add(items);
		}};
		this.condition = condition;
		this.onSuccess = onSuccess;
	}

	public LootPool SetItems(List<ItemStack> items)
	{
		this.items = items;
		return this;
	}

	public LootPool SetItems(ItemStack... items)
	{
		this.items = Arrays.asList(items);
		return this;
	}

	public LootPool SetCondition(Predicate<LootData> condition)
	{
		this.condition = condition;
		return this;
	}

	public LootPool SetOnSuccess(Consumer<LootData> onSuccess)
	{
		this.onSuccess = onSuccess;
		return this;
	}
}
