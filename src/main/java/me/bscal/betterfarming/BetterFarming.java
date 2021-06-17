package me.bscal.betterfarming;

import me.bscal.betterfarming.common.listeners.LootManagerListener;
import me.bscal.betterfarming.common.listeners.PlayerBlockBreakListener;
import me.bscal.betterfarming.common.listeners.ServerEntityCombatListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
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

	@Override
	public void onInitialize()
	{

		PlayerBlockBreakEvents.AFTER.register(new PlayerBlockBreakListener());
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(new ServerEntityCombatListener());

		LootManagerListener LMListener = new LootManagerListener();
		//LootManagerEarlyAssignCallback.EARLY_ASSIGN.register(LMListener);
		LootTableLoadingCallback.EVENT.register(LMListener);

	}
}
