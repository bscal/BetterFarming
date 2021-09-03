package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.database.blockdata.CropDataBlockHandler;
import me.bscal.betterfarming.common.utils.Utils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

public class ServerTickListener implements ServerTickEvents.EndTick
{

	private static final int UPDATE_TIME = 60 * 20;

	/**
	 * The average time a random tick takes / the UPDATE_TIME. Used to "progress" time for unloaded blocks with RANDOM_TICK_SPEED GameRule
	 */
	private float m_randomTickChance;

	@Override
	public void onEndTick(MinecraftServer server)
	{
		BetterFarming.RUN_SCHEDULER.Tick(server.getTicks());
		BetterFarming.DELAY_SCHEDULER.Tick(server.getTicks());

		if (server.getTicks() % UPDATE_TIME == 0)
		{
			BetterFarming.TICK_SPEED = server.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
		}
	}
}
