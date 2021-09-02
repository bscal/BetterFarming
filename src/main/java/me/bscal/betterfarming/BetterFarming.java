package me.bscal.betterfarming;

import me.bscal.betterfarming.common.commands.SeasonCommand;
import me.bscal.betterfarming.common.config.TestConfig;
import me.bscal.betterfarming.common.config.TestConfigBlock;
import me.bscal.betterfarming.common.database.blockdata.BlockData;
import me.bscal.betterfarming.common.database.blockdata.BlockDataManager;
import me.bscal.betterfarming.common.database.blockdata.blocks.TestDataBlock;
import me.bscal.betterfarming.common.database.blockdata.smart.SmartDataManager;
import me.bscal.betterfarming.common.database.blockdata.worldpos.WorldPosDataManager;
import me.bscal.betterfarming.common.generation.Generators;
import me.bscal.betterfarming.common.listeners.*;
import me.bscal.betterfarming.common.loot.override.Lootables;
import me.bscal.betterfarming.common.registries.ItemRegistry;
import me.bscal.betterfarming.common.seasons.*;
import me.bscal.betterfarming.common.utils.Utils;
import me.bscal.betterfarming.common.utils.schedulers.FastDelayScheduler;
import me.bscal.betterfarming.common.utils.schedulers.FastRunnableScheduler;
import me.bscal.betterfarming.common.utils.schedulers.SchedulableOwner;
import me.bscal.betterfarming.common.utils.schedulers.example.TestPersistentSchedulable;
import net.devtech.arrp.api.RRPCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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
	public static final boolean CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	public static final Random RAND = new Random();
	public static final int UPDATE_DELAY = 20 * 30;
	public static final SeasonSettings SEASON_SETTINGS = new SeasonSettings();
	public static final SeasonsRegistry SEASONS_REGISTRY = new SeasonsRegistry();
	public static final SeasonCropManager CROP_MANAGER = new SeasonCropManager();
	public static final SeasonClock SEASON_CLOCK = new SeasonClock();
	public static final Identifier SYNC_PACKET = new Identifier(MOD_ID, "sync_time");

	private static MinecraftServer m_server;
	private static ServerWorld m_overWorldReference; // Cached for getTime() calls

	public static TestConfig config;

	public static WorldPosDataManager WORLD_DATAMANGER;
	public static final FastRunnableScheduler RUN_SCHEDULER = new FastRunnableScheduler();
	public static final FastDelayScheduler DELAY_SCHEDULER = new FastDelayScheduler();

	@Override
	public void onInitialize()
	{
		config = new TestConfig(Utils.GetStringPathInConfig("test_config.json"), new TestConfigBlock());

		RRPCallback.BEFORE_VANILLA.register(pack -> pack.add(Generators.RESOURCE_PACK));
		ItemRegistry.Register();
		Generators.Init();

		Lootables.RegisterLootables();

		SEASON_SETTINGS.saveConfigToFile();
		SeasonCropManager.GenerateDefaults(Utils.GetPathInConfig("seasonal_crops.json").toString());
		CROP_MANAGER.Load(Utils.GetPathInConfig("seasonal_crops.json").toString());

		//BLOCK_DATA.Load(Utils.GetStringPathInConfig("block_data.json"));

		CommandRegistrationCallback.EVENT.register(new SeasonCommand());
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			m_server = server;
		});
		ServerLifecycleEvents.SERVER_STOPPING.register((server -> {
			RUN_SCHEDULER.Save(server);
			DELAY_SCHEDULER.Save(server);
			WORLD_DATAMANGER.Save();
		}));
		ServerWorldEvents.LOAD.register(((server, world) -> {
			if (world.getRegistryKey().equals(World.OVERWORLD))
			{
				WORLD_DATAMANGER = new WorldPosDataManager("world_blocks", world, BlockData::new);
				m_overWorldReference = world;
				SeasonManager.GetOrCreate(world);
				SEASONS_REGISTRY.Load(world);
				RUN_SCHEDULER.Load(server);
				DELAY_SCHEDULER.Load(server);

				DELAY_SCHEDULER.ScheduleRunnable(100, false, false, (delayEntry) ->
				{
					BetterFarming.LOGGER.info("DELAY FIRST!");
					return false;
				});

				DELAY_SCHEDULER.ScheduleRunnable(150, false, false, (delayEntry) ->
				{
					BetterFarming.LOGGER.info("DELAY SECOND!");
					return false;
				});
			}
		}));
		ServerWorldEvents.UNLOAD.register(((server, world) -> {

		}));
		PlayerBlockBreakEvents.AFTER.register(new PlayerBlockBreakListener());
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(new ServerEntityCombatListener());
		ServerTickEvents.END_SERVER_TICK.register(new ServerTickListener());

		LootManagerListener LMListener = new LootManagerListener();
		//LootManagerEarlyAssignCallback.EARLY_ASSIGN.register(LMListener);
		LootTableLoadingCallback.EVENT.register(LMListener);

		ChunkListeners cl = new ChunkListeners();
		ServerChunkEvents.CHUNK_LOAD.register(cl);
		ServerChunkEvents.CHUNK_UNLOAD.register(cl);

	}

	public static Identifier prefixPath(Identifier identifier, String prefix) {
		return new Identifier(identifier.getNamespace(), prefix + '/' + identifier.getPath());
	}

	public static MinecraftServer GetServer()
	{
		return m_server;
	}

	public static long GetTime()
	{
		return m_overWorldReference.getTime();
	}

	public static void Log(String msg)
	{
		LOGGER.info(msg);
	}
}
