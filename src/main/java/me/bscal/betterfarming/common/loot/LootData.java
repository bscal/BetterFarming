package me.bscal.betterfarming.common.loot;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class LootData
{
	public final LivingEntity entity;
	public final LivingEntity attacker;
	public final ServerWorld world;
	public int bonusRolls;
	public List<ItemStack> items;

	public LootData(LivingEntity entity, LivingEntity attacker, ServerWorld world)
	{
		this.entity = entity;
		this.attacker = attacker;
		this.world = world;
	}

	public void CopyItems(final List<ItemStack> src)
	{
		items = new ArrayList<>(src.size());
		src.forEach(itemStack -> items.add(itemStack.copy()));
	}
}
