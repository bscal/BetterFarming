package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.common.seasons.SeasonManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class ServerTickListener implements ServerTickEvents.EndTick
{

	@Override
	public void onEndTick(MinecraftServer server)
	{
		//SeasonManager.GetOrCreate(server.getOverworld()).Update();
	}
}
