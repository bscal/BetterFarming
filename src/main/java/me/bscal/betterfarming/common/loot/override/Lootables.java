package me.bscal.betterfarming.common.loot.override;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class Lootables
{

	private Lootables()
	{
	}

	public static void RegisterLootables()
	{
		Random rand = new Random();

		LootOverrideManager.Get().RegisterLootable(Registry.BLOCK.getId(Blocks.GRASS_BLOCK), new BlockLootable()
		{
			@Override
			public List<ItemStack> Generate(BlockState state, LootContext context, ServerWorld world, BlockPos origin,
					Optional<BlockEntity> blockEntity, Optional<ItemStack> tool, Optional<Entity> entity)
			{
				List<ItemStack> loot = new ArrayList<>(2);
				loot.add(new ItemStack(Blocks.DIRT));

				if (rand.nextFloat() < .50)
					loot.add(new ItemStack(Items.GOLD_INGOT));

				return loot;
			}
		});
	}

}
