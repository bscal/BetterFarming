package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.common.database.blockdata.BlockDataManager;
import me.bscal.betterfarming.common.utils.Utils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

public class ServerTickListener implements ServerTickEvents.EndTick
{

	private static final int UPDATE_TIME = 20 * 10;

	private int m_clock;
	/**
	 * The average time a random tick takes / the UPDATE_TIME. Used to "progress" time for unloaded blocks with RANDOM_TICK_SPEED GameRule
	 */
	private float m_randomTickChance;

	@Override
	public void onEndTick(MinecraftServer server)
	{
		if (m_clock++ > UPDATE_TIME)
		{
			m_clock = 0;
			if (m_randomTickChance < 1)
			{
				m_randomTickChance = UPDATE_TIME / Utils.GeometricDistributionMeanForRandomTicks(
						server.getGameRules().get(GameRules.RANDOM_TICK_SPEED).get());
			}
			server.getWorlds().forEach((world -> {
				BlockDataManager.GetOrCreate(world).UpdateUnloadedEntries(server, m_randomTickChance);
			}));
		}
	}
}
