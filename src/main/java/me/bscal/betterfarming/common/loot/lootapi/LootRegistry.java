package me.bscal.betterfarming.common.loot.lootapi;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.bscal.betterfarming.common.items.SickleItem;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class LootRegistry
{

	private static final Object2ObjectOpenHashMap<Identifier, LootTable> m_idToLootTable = new Object2ObjectOpenHashMap<>();


	public static void Register(Identifier id, LootTable table)
	{
		m_idToLootTable.put(id, table);
	}

	public static LootTable GetLootTable(Identifier id)
	{
		return m_idToLootTable.get(id);
	}

	static
	{
		LootTable wheatTable = new LootTable("wheat", false, 1);
		wheatTable.AddDynamicDrops(ctx -> {
			List<LootDrop> res = new ArrayList<>();
			if (ctx instanceof BlockLootContext blockCtx)
			{
				int age = blockCtx.state.get(CropBlock.AGE);
				if (age >= CropBlock.MAX_AGE)
				{
					if (blockCtx.tool != null)
					{
						if (blockCtx.tool.isIn(FabricToolTags.HOES))
						{
						}
						else if (blockCtx.tool.getItem() instanceof SickleItem)
						{

						}
					}
				}
			}
			return res;
		});
		wheatTable.AddDrop(new LootDrop(1.0f, false, true, true, LootItem.Of(new ItemStack(Items.WHEAT_SEEDS, 1)));
		Register(Blocks.WHEAT.getLootTableId(), wheatTable);
	}

}
