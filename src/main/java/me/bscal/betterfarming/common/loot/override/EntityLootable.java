package me.bscal.betterfarming.common.loot.override;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;

public class EntityLootable
{

	public List<ItemStack> Generate(LivingEntity sourceEntity, LootContext context, DamageSource source, boolean causedByPlayer,
			Identifier lootId, LootTable lootTable, ServerWorld world)
	{
		return null;
	}

}
