package me.bscal.betterfarming;

import me.bscal.betterfarming.common.commands.SeasonCommand;
import me.bscal.betterfarming.common.database.BetterFarmingDatabase;
import me.bscal.betterfarming.common.events.SeasonEvents;
import me.bscal.betterfarming.common.listeners.LootManagerListener;
import me.bscal.betterfarming.common.listeners.PlayerBlockBreakListener;
import me.bscal.betterfarming.common.listeners.ServerEntityCombatListener;
import me.bscal.betterfarming.common.listeners.ServerTickListener;
import me.bscal.betterfarming.common.seasons.SeasonClock;
import me.bscal.betterfarming.common.seasons.SeasonManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;

public class BetterFarming implements ModInitializer
{

	public static final String MOD_ID = "betterfarming";
	public static final String MOD_NAME = "BetterFarming";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static final boolean DEBUG = true;
	public static final Random RAND = new Random();
	public static final int UPDATE_DELAY = 20 * 30;

	public static final Identifier SYNC_PACKET = new Identifier(MOD_ID, "sync_time");

	private static MinecraftServer m_server;


	@Override
	public void onInitialize()
	{
		BetterFarmingDatabase.CreateTables();

		CommandRegistrationCallback.EVENT.register(new SeasonCommand());

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> m_server = server);
		ServerWorldEvents.LOAD.register(((server, world) -> SeasonManager.GetOrCreate(world)));
		PlayerBlockBreakEvents.AFTER.register(new PlayerBlockBreakListener());
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(new ServerEntityCombatListener());
		ServerTickEvents.END_SERVER_TICK.register(new ServerTickListener());

		LootManagerListener LMListener = new LootManagerListener();
		//LootManagerEarlyAssignCallback.EARLY_ASSIGN.register(LMListener);
		LootTableLoadingCallback.EVENT.register(LMListener);
	}

	public static Optional<MinecraftServer> GetServer()
	{
		return (m_server == null) ? Optional.empty() : Optional.of(m_server);
	}

	public static void Log(String msg)
	{
		LOGGER.info(msg);
	}
}
