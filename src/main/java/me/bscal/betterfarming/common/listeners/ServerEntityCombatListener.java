package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.common.loot.LootData;
import me.bscal.betterfarming.common.loot.LootRegister;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.world.ServerWorld;

public class ServerEntityCombatListener implements ServerEntityCombatEvents.AfterKilledOtherEntity
{
	@Override
	public void afterKilledOtherEntity(ServerWorld world, Entity entity, LivingEntity killedEntity)
	{
		if (!(entity instanceof LivingEntity))
			return;

		if (killedEntity instanceof CowEntity)
		{
			LootRegister.COW_TABLE.RollAndDrop(new LootData(killedEntity, (LivingEntity) entity, world));
		}
	}
}
