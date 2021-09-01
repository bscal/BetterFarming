package me.bscal.betterfarming.common.loot.lootapi;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.bscal.betterfarming.common.items.SickleItem;
import me.bscal.betterfarming.common.utils.RandomQuantity;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
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
			if (ctx instanceof BlockLootContext blockCtx && blockCtx.entity instanceof LivingEntity livingEnt)
			{
				int age = blockCtx.state.get(CropBlock.AGE);
				if (age >= CropBlock.MAX_AGE)
				{
					if (blockCtx.tool != null)
					{
						if (blockCtx.tool.isIn(FabricToolTags.HOES))
						{
							res.add(LootDrop.Of(Items.WHEAT, 1 + RandomQuantity.Bonus(1, .2f)));
							res.add(LootDrop.Of(Items.WHEAT_SEEDS, RandomQuantity.Rand(0, 1)));
						}
						else if (blockCtx.tool.getItem() instanceof SickleItem)
						{
							res.add(LootDrop.Of(Items.WHEAT, 1 + RandomQuantity.Probability(new RandomQuantity.ProbabilityPair(1, 75f),
									new RandomQuantity.ProbabilityPair(2, 25f))));
							res.add(LootDrop.Of(Items.GRASS, RandomQuantity.Rand(1, 3)));
						}
						else
							return LootTable.ItemStackToLootDrop(blockCtx.minecraftLootTable.generateLoot(blockCtx.minecraftLootContext));
					}
				}
				else
				{
					res.add(LootDrop.Of(Items.WHEAT_SEEDS, RandomQuantity.Pick(0, 1, 1)));
				}
			}
			return res;
		});

		Register(Blocks.WHEAT.getLootTableId(), wheatTable);
	}

}
