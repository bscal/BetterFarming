package me.bscal.betterfarming.common.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.components.entity.EntityEcoProvider;
import me.bscal.betterfarming.common.components.entity.IEntityEcoComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Optional;
import java.util.Set;

public class EcoEntityLootCondition implements LootCondition
{

	final EntityPredicate predicate;
	final LootContext.EntityTarget entity;

	LootConditionType ECO_ENTITY_LOOT_CONDITION = register("eco_loot",
			new EcoEntityLootCondition.Serializer());

	public EcoEntityLootCondition(EntityPredicate predicate, LootContext.EntityTarget entity)
	{
		this.predicate = predicate;
		this.entity = entity;
	}

	private static LootConditionType register(String id, JsonSerializer<? extends LootCondition> serializer)
	{
		return (LootConditionType) Registry.register(Registry.LOOT_CONDITION_TYPE,
				new Identifier(BetterFarming.MOD_ID, id), new LootConditionType(serializer));
	}

	@Override
	public LootConditionType getType()
	{
		return ECO_ENTITY_LOOT_CONDITION;
	}

	@Override
	public boolean test(LootContext lootContext)
	{
		Entity entity = (Entity) lootContext.get(this.entity.getParameter());
		if (entity instanceof LivingEntity)
		{
			LivingEntity livingEntity = (LivingEntity) entity;
			Optional<IEntityEcoComponent> ecoComponent = EntityEcoProvider.ENTITY_ECO.maybeGet(livingEntity);
			if (ecoComponent.isPresent())
			{
				Vec3d vec3d = (Vec3d) lootContext.get(LootContextParameters.ORIGIN);
				return this.predicate.test(lootContext.getWorld(), vec3d, entity);
			}
		}
		return false;
	}

	public Set<LootContextParameter<?>> getRequiredParameters()
	{
		return ImmutableSet.of(LootContextParameters.ORIGIN, this.entity.getParameter());
	}

	public static LootCondition.Builder builder(LootContext.EntityTarget entity,
			EntityPredicate.Builder predicateBuilder)
	{
		return () -> new EcoEntityLootCondition(predicateBuilder.build(), entity);
	}

	public static LootCondition.Builder builder(LootContext.EntityTarget entity, EntityPredicate predicate)
	{
		return () -> new EcoEntityLootCondition(predicate, entity);
	}

	public static class Serializer implements JsonSerializer<EcoEntityLootCondition>
	{
		public void toJson(JsonObject jsonObject, EcoEntityLootCondition ecoEntityLootCondition,
				JsonSerializationContext jsonSerializationContext)
		{
			jsonObject.add("predicate", ecoEntityLootCondition.predicate.toJson());
			jsonObject.add("entity", jsonSerializationContext.serialize(ecoEntityLootCondition.entity));
		}

		public EcoEntityLootCondition fromJson(JsonObject jsonObject,
				JsonDeserializationContext jsonDeserializationContext)
		{
			EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("predicate"));
			return new EcoEntityLootCondition(entityPredicate,
					(LootContext.EntityTarget) JsonHelper.deserialize(jsonObject, "entity",
							jsonDeserializationContext, LootContext.EntityTarget.class));
		}
	}
}
