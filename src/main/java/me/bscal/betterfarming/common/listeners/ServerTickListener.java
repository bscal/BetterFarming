package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.schedulers.FastDelayScheduler;
import me.bscal.betterfarming.common.utils.schedulers.FastIntervalScheduler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

public class ServerTickListener implements ServerTickEvents.EndTick
{
	private static final int UPDATE_TIME = 60 * 20;

	@Override
	public void onEndTick(MinecraftServer server)
	{
		FastIntervalScheduler.INSTANCE.Tick(server.getTicks());
		FastDelayScheduler.INSTANCE.Tick(server.getTicks());

		if (server.getTicks() % UPDATE_TIME == 0)
		{
			BetterFarming.TICK_SPEED = server.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
		}
	}
}
