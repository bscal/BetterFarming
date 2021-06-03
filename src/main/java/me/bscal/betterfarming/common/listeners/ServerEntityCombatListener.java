package me.bscal.betterfarming.common.listeners;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class ServerEntityCombatListener implements ServerEntityCombatEvents.AfterKilledOtherEntity
{
	@Override
	public void afterKilledOtherEntity(ServerWorld world, Entity entity, LivingEntity killedEntity)
	{
	}
}
