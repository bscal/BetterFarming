package me.bscal.betterfarming.client;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.client.commands.ClientCommandRegister;
import me.bscal.betterfarming.client.particles.FallingLeavesParticle;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import me.bscal.betterfarming.common.utils.TimerList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

import java.math.BigInteger;

@Environment(EnvType.CLIENT) public class BetterFarmingClient implements ClientModInitializer
{

	public static final ParticleType<BlockStateParticleEffect> FALLING_LEAVES = FabricParticleTypes.complex(BlockStateParticleEffect.PARAMETERS_FACTORY);


	private static final BiomeSeasonHandler SEASON_HANDLER = new BiomeSeasonHandler();

	@Override
	public void onInitializeClient()
	{
		Registry.register(Registry.PARTICLE_TYPE, BetterFarming.MOD_ID + ":falling_leaves", FALLING_LEAVES);
		ParticleFactoryRegistry.getInstance().register(FALLING_LEAVES, FallingLeavesParticle.Factory::new);

		ClientPlayNetworking.registerGlobalReceiver(BetterFarming.SYNC_PACKET, BiomeSeasonHandler.SyncTimeS2CPacketHandler());

		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			SEASON_HANDLER.RegisterBiomeChangers(client.world);
			BetterFarming.SEASONS_REGISTRY.Load(client.world);
		}));
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			BetterFarming.SEASONS_REGISTRY.Unload();
		}));
		ClientTickEvents.END_WORLD_TICK.register((world -> {
			if (!SEASON_HANDLER.receivedSyncPacket) // Instead of sending packet every we tick we can simulate time passing.
				SEASON_HANDLER.seasonClock.ticksSinceCreation++;
		}));

		ClientCommandRegister.Register();
	}

	public static BiomeSeasonHandler GetBiomeSeasonHandler()
	{
		return SEASON_HANDLER;
	}
}
