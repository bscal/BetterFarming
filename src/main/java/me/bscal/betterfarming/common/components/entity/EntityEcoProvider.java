package me.bscal.betterfarming.common.components.entity;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.bscal.betterfarming.BetterFarming;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class EntityEcoProvider implements EntityComponentInitializer
{

	public static final Identifier ENTITY_ECO_ID = new Identifier(BetterFarming.MOD_ID, "entity_eco");
	public static final ComponentKey<IEntityEcoComponent> ENTITY_ECO = ComponentRegistry.getOrCreate(
			ENTITY_ECO_ID, IEntityEcoComponent.class);

	public static final List<Class<? extends LivingEntity>> MOB_ENTITY_REGISTRY = new ArrayList<>();

	static
	{
		MOB_ENTITY_REGISTRY.add(CowEntity.class);
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
	{
		for (Class<? extends LivingEntity> clazz : MOB_ENTITY_REGISTRY)
		{
			registry.registerFor(clazz, ENTITY_ECO, EntityEcoComponent::new);
		}
	}
}
