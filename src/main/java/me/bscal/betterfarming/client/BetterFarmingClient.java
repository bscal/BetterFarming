package me.bscal.betterfarming.client;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.client.commands.ClientCommandRegister;
import me.bscal.betterfarming.client.particles.FallingLeavesParticle;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import me.bscal.betterfarming.common.utils.zones.BlockIndications;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.util.Set;

@Environment(EnvType.CLIENT) public class BetterFarmingClient implements ClientModInitializer
{

	public static final ParticleType<BlockStateParticleEffect> FALLING_LEAVES = FabricParticleTypes.complex(
			BlockStateParticleEffect.PARAMETERS_FACTORY);

	private static BiomeSeasonHandler SEASON_HANDLER;

	@Override
	public void onInitializeClient()
	{
		Registry.register(Registry.PARTICLE_TYPE, BetterFarming.MOD_ID + ":falling_leaves", FALLING_LEAVES);
		ParticleFactoryRegistry.getInstance().register(FALLING_LEAVES, FallingLeavesParticle.Factory::new);

		ClientPlayNetworking.registerGlobalReceiver(BetterFarming.SYNC_PACKET, BiomeSeasonHandler.SyncTimeS2CPacketHandler());

		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			SEASON_HANDLER = new BiomeSeasonHandler(client.world);
			BetterFarming.SEASONS_REGISTRY.Load(client.world);
		}));
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			BetterFarming.SEASONS_REGISTRY.Unload();
		}));
		ClientTickEvents.END_WORLD_TICK.register((world -> {
			if (!SEASON_HANDLER.receivedSyncPacket) // Instead of sending packet every we tick we can simulate time passing.
				SEASON_HANDLER.seasonClock.ticksSinceCreation++;

			BlockPos pos = MinecraftClient.getInstance().player.getBlockPos();

			//BlockIndications.DrawBlockBorders(world, Set.of(pos, pos.east(), pos.north(), pos.south(), pos.west()), new Vec3f(0.0f, 0.0f, 1.0f), 1.0f);
			//BlockIndications.DrawBlockOutline(world, pos, new Vec3f(1.0f, 0.0f, 0.0f), 1.0f);
			//BlockIndications.FillBlock(world, pos.east(), 5, new Vec3f(0.0f, 1.0f, 0.0f), 1.0f);
			//BlockIndications.DrawBlockCone(world, pos, MinecraftClient.getInstance().player.getHorizontalFacing(), 5, 1, 2, new Vec3f(.25f, .25f, .25f),
			//		1.0f);
			BlockIndications.DrawBlockCircle(world, pos, 5, new Vec3f(0.0f, 0.35f, 0.1f), 1.0f);
			BlockIndications.DrawCircle(world, Vec3d.of(pos.north().north().north()), 3, 20, new Vec3f(0.0f, 0.5f, 0.5f), 1.0f);
		}));

		ClientCommandRegister.Register();
	}

	public static BiomeSeasonHandler GetBiomeSeasonHandler()
	{
		return SEASON_HANDLER;
	}
}
