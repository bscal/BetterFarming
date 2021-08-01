package me.bscal.betterfarming.client;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.client.commands.BiomeInfoCommand;
import me.bscal.betterfarming.client.commands.ColorDumpCommand;
import me.bscal.betterfarming.client.commands.ReloadColorsCommand;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT) public class BetterFarmingClient implements ClientModInitializer
{

	private static BiomeSeasonHandler m_seasonHandler;

	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(BetterFarming.SYNC_PACKET,
				BiomeSeasonHandler.SyncTimeS2CPacketHandler());

		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			m_seasonHandler = new BiomeSeasonHandler();
			m_seasonHandler.RegisterBiomeChangers(client.world);
			BetterFarming.SEASONS_REGISTRY.Load(client.world);
		}));
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			m_seasonHandler = null;
			BetterFarming.SEASONS_REGISTRY.Unload();
		}));
		ClientTickEvents.END_WORLD_TICK.register((world -> {
			if (!m_seasonHandler.recievedSyncPacket) // Instead of sending packet every we tick we can simulate time passing.
				m_seasonHandler.seasonClock.ticksSinceCreation++;
		}));

		ColorDumpCommand.Register(m_seasonHandler);
		ReloadColorsCommand.Register(m_seasonHandler);
		BiomeInfoCommand.Register(m_seasonHandler);
	}

	public static BiomeSeasonHandler GetBiomeSeasonHandler()
	{
		return m_seasonHandler;
	}
}
