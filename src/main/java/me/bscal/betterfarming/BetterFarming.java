package me.bscal.betterfarming;

import me.bscal.betterfarming.common.commands.SeasonCommand;
import me.bscal.betterfarming.common.database.blockdata.BlockDataManager;
import me.bscal.betterfarming.common.listeners.LootManagerListener;
import me.bscal.betterfarming.common.listeners.PlayerBlockBreakListener;
import me.bscal.betterfarming.common.listeners.ServerEntityCombatListener;
import me.bscal.betterfarming.common.listeners.ServerTickListener;
import me.bscal.betterfarming.common.seasons.*;
import me.bscal.betterfarming.common.utils.Utils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class BetterFarming implements ModInitializer
{

	public static final String MOD_ID = "betterfarming";
	public static final String MOD_NAME = "BetterFarming";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static final boolean DEBUG = true;
	public static final Random RAND = new Random();
	public static final int UPDATE_DELAY = 20 * 30;
	public static final SeasonSettings SEASON_SETTINGS = new SeasonSettings();
	public static final SeasonsRegistry SEASONS_REGISTRY = new SeasonsRegistry();
	public static final SeasonCropManager CROP_MANAGER = new SeasonCropManager();
	public static final SeasonClock SEASON_CLOCK = new SeasonClock();
	public static final Identifier SYNC_PACKET = new Identifier(MOD_ID, "sync_time");

	private static MinecraftServer m_server;

	@Override
	public void onInitialize()
	{
		SeasonCropManager.GenerateDefaults(Utils.GetPathInConfig("seasonal_crops.json").toString());
		CROP_MANAGER.Load(Utils.GetPathInConfig("seasonal_crops.json").toString());

		//BLOCK_DATA.Load(Utils.GetStringPathInConfig("block_data.json"));

		CommandRegistrationCallback.EVENT.register(new SeasonCommand());
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> m_server = server);
		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
			//BLOCK_DATA.Save(Utils.GetStringPathInConfig("block_data.json"));
		});
		ServerWorldEvents.LOAD.register(((server, world) -> {
			if (world.getRegistryKey().equals(World.OVERWORLD))
			{
				SeasonManager.GetOrCreate(world);
				BlockDataManager.GetOrCreate(world);
				SEASONS_REGISTRY.Load(world);
			}
		}));
		PlayerBlockBreakEvents.AFTER.register(new PlayerBlockBreakListener());
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(new ServerEntityCombatListener());
		ServerTickEvents.END_SERVER_TICK.register(new ServerTickListener());

		LootManagerListener LMListener = new LootManagerListener();
		//LootManagerEarlyAssignCallback.EARLY_ASSIGN.register(LMListener);
		LootTableLoadingCallback.EVENT.register(LMListener);

	}

	public static MinecraftServer GetServer()
	{
		return m_server;
	}

	public static void Log(String msg)
	{
		LOGGER.info(msg);
	}
}
