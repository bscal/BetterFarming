package me.bscal.betterfarming.common.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT) public interface ClientWorldEvent
{

	Event<ClientJoinWorld> CLIENT_JOIN_WORLD_EVENT = EventFactory.createArrayBacked(ClientJoinWorld.class,
			listeners -> (client, world) -> {
				for (ClientJoinWorld listener : listeners)
				{
					listener.OnClientJoinWorld(client, world);
				}
			});

	Event<ClientDisconnectWorld> CLIENT_DISCONNECT_WORLD_EVENT = EventFactory.createArrayBacked(
			ClientDisconnectWorld.class, listeners -> (client, world) -> {
				for (ClientDisconnectWorld listener : listeners)
				{
					listener.OnClientDisconnectWorld(client, world);
				}
			});

	@FunctionalInterface interface ClientJoinWorld
	{
		void OnClientJoinWorld(MinecraftClient client, ClientWorld world);
	}

	@FunctionalInterface interface ClientDisconnectWorld
	{
		void OnClientDisconnectWorld(MinecraftClient client, ClientWorld world);
	}

}
