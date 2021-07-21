package me.bscal.betterfarming.client;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.client.seasons.biome.BiomeEffectHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;

@Environment(EnvType.CLIENT) public class BetterFarmingClient implements ClientModInitializer
{

	public static BiomeEffectHandler m_effectHandler;

	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(BetterFarming.SYNC_PACKET, m_effectHandler.SyncTime());
		//ClientPlayNetworking.registerGlobalReceiver(BetterFarming.SEASON_PACKET,
		//m_effectHandler.SyncSeasonChange());
		//m_effectHandler.UpdateSeasonColors();

		ClientTickEvents.END_WORLD_TICK.register((world -> {
			if (m_effectHandler.reloadWorld)
			{
				m_effectHandler.UpdateSeasonColors();
				MinecraftClient.getInstance().worldRenderer.reload();
				m_effectHandler.reloadWorld = false;
			}
		}));
	}
}
