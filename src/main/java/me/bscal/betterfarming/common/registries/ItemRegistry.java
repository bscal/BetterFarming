package me.bscal.betterfarming.common.registries;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.generation.Generators;
import me.bscal.betterfarming.common.items.SickleItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistry
{

	public static final Item IRON_SICKLE = new SickleItem(0, 2, -2f, ToolMaterials.IRON, new FabricItemSettings().group(ItemGroup.TOOLS));
	public static final Item GOLD_SICKLE = new SickleItem(1, 2, -1.8f, ToolMaterials.GOLD,
			new FabricItemSettings().group(ItemGroup.TOOLS));
	public static final Item DIAMOND_SICKLE = new SickleItem(1, 2, -1.6f, ToolMaterials.DIAMOND,
			new FabricItemSettings().group(ItemGroup.TOOLS));

	private static Item register(String id, Item item, String en_us)
	{
		return register(new Identifier(id), item, en_us);
	}

	private static Item register(Identifier id, Item item, String en_us)
	{
		if (item instanceof BlockItem)
		{
			((BlockItem) item).appendBlocks(Item.BLOCK_ITEMS, item);
		}

		Generators.register(Generators.DEFAULT, item, id, en_us);

		return Registry.register(Registry.ITEM, id, item);
	}

	public static void Register()
	{
		register(new Identifier(BetterFarming.MOD_ID, "iron_sickle"), IRON_SICKLE, "Iron Sickle");
		register(new Identifier(BetterFarming.MOD_ID, "gold_sickle"), GOLD_SICKLE, "Gold Sickle");
		register(new Identifier(BetterFarming.MOD_ID, "diamond_sickle"), DIAMOND_SICKLE, "Diamond Sickle");
	}

}
