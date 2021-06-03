package me.bscal.betterfarming.common.loot;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class LootRegister
{

	//public static final LootTable COW_TABLE = new LootTable(new Identifier(BetterFarming.MOD_ID, "cow_loot"));

	public static final LootDrops COW_DROP = new LootDrops(1).AddEntry(
			new LootEntry(1, new LootPool(new ItemStack(Items.EGG), (data) -> {
				BetterFarming.LOGGER.info("condition");
				return true;
			}, (data) -> {
				data.items.add(new ItemStack(Items.COOKED_BEEF));
				BetterFarming.LOGGER.info("supplier");
			}))).AddEntry(
			new LootEntry(1, new LootPool(new ItemStack(Items.ACACIA_LOG), null, null)));

}
