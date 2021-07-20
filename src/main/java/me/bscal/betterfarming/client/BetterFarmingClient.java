package me.bscal.betterfarming.client;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.client.seasons.biome.BiomeEffectHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT) public class BetterFarmingClient implements ClientModInitializer
{

	private final BiomeEffectHandler m_effectHandler = new BiomeEffectHandler();

	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(BetterFarming.SYNC_PACKET, m_effectHandler.SyncTime());
		ClientPlayNetworking.registerGlobalReceiver(BetterFarming.SEASON_PACKET,
				m_effectHandler.SyncSeasonChange());
		//m_effectHandler.UpdateSeasonColors();
	}
}
