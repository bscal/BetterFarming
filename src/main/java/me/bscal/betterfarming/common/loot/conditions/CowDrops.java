package me.bscal.betterfarming.common.loot.conditions;

import me.bscal.betterfarming.common.components.entity.EntityEcoProvider;
import me.bscal.betterfarming.common.components.entity.IEntityEcoComponent;
import me.bscal.betterfarming.common.loot.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CowDrops implements ILootDrops
{
	@Override
	public List<ItemStack> Roll(LootData data)
	{
		List<ItemStack> items = new ArrayList<>();
		Optional<IEntityEcoComponent> component = EntityEcoProvider.ENTITY_ECO.maybeGet(data.entity);
		if (component.isPresent())
		{
			int happiness = component.get().GetHappiness();
			int growthState = component.get().GetGrowthStage();
			int condition = component.get().GetCondition();
			int fatness = component.get().GetFatness();

			float baseChance = .1f + .1f * happiness;
			int meatAmount = LootUtils.RollForBonus(1, 2, 1, baseChance);
			int leatherAmount = LootUtils.RollForBonus(0, 1, 1, baseChance);

			meatAmount += fatness;
			if (growthState > 3)
				leatherAmount += LootUtils.RollForBonus(0, condition, 1, baseChance);

			if (happiness < 1)
			{
				meatAmount--;
				leatherAmount--;
			}

			LootUtils.TryCreateItemStack(items, Items.BEEF, meatAmount);
			LootUtils.TryCreateItemStack(items, Items.LEATHER, leatherAmount);
		}
		return items;
	}
}
