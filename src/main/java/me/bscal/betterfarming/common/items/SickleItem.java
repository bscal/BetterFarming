package me.bscal.betterfarming.common.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.loot.LootRegister;
import me.bscal.betterfarming.common.loot.lootapi.LootRegistry;
import me.bscal.betterfarming.common.loot.override.LootOverrideManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SickleItem extends ToolItem
{

	private final float m_lootBonus;
	private final float m_attackDamage;
	private final Multimap<EntityAttribute, EntityAttributeModifier> m_attributeModifiers;

	public SickleItem(float lootBonus, float attackDamage, float attackSpeed, ToolMaterial material, Item.Settings settings)
	{
		super(material, settings);
		this.m_lootBonus = lootBonus;
		this.m_attackDamage = attackDamage + material.getAttackDamage();
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
				new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", this.m_attackDamage,
						EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
				new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", attackSpeed,
						EntityAttributeModifier.Operation.ADDITION));
		this.m_attributeModifiers = builder.build();
	}


	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		if (context.getWorld() instanceof ServerWorld world && !world.isClient)
		{
			BlockState state = world.getBlockState(context.getBlockPos());

			if (state.getBlock() instanceof CropBlock)
			{
				LootOverrideManager.Get().OverrideLoot(GenerateLoots(world.random), world, context.getBlockPos(), Blocks.AIR::getDefaultState);
			}

		}

		return super.useOnBlock(context);
	}

	private List<ItemStack> GenerateLoots(Random rand)
	{
		// TODO
		return null;

	}
}
