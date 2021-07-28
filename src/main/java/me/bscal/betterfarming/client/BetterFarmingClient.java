package me.bscal.betterfarming.client;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.client.commands.ColorDumpCommand;
import me.bscal.betterfarming.client.commands.ReloadColorsCommand;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import me.bscal.betterfarming.common.events.ClientWorldEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT) public class BetterFarmingClient implements ClientModInitializer
{

	private static final BiomeSeasonHandler m_seasonHandler = new BiomeSeasonHandler();

	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(BetterFarming.SYNC_PACKET,
				BiomeSeasonHandler.SyncTimeS2CPacketHandler());

		ClientWorldEvent.CLIENT_JOIN_WORLD_EVENT.register(
				((client, world) -> m_seasonHandler.RegisterBiomeChangers(world)));

		ClientTickEvents.END_WORLD_TICK.register((world -> {
			if (!m_seasonHandler.recievedSyncPacket) // Instead of sending packet every we tick we can simulate time passing.
				m_seasonHandler.seasonClock.ticksSinceCreation++;
		}));

		ColorDumpCommand.Register(m_seasonHandler);
		ReloadColorsCommand.Register(m_seasonHandler);
	}

	public static int GetSeason()
	{
		return m_seasonHandler.seasonClock.currentSeason;
	}

	public static BiomeSeasonHandler GetBiomeSeasonHandler()
	{
		return m_seasonHandler;
	}
}
