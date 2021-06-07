package me.bscal.betterfarming.common.components.entity;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.components.entity.types.CowEntityEcoComponent;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

public final class EntityEcoProvider implements EntityComponentInitializer
{

	public static final Identifier ENTITY_ECO_ID = new Identifier(BetterFarming.MOD_ID, "entity_eco");
	public static final ComponentKey<IEntityEcoComponent> ENTITY_ECO = ComponentRegistryV3.INSTANCE.getOrCreate(
			ENTITY_ECO_ID, IEntityEcoComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
	{
		registry.registerFor(CowEntity.class, ENTITY_ECO, CowEntityEcoComponent::new);
	}
}
