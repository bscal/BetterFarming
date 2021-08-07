package me.bscal.betterfarming.client.commands;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

public final class ReloadColorsCommand implements ClientCommand
{

	public void Register()
	{
		ClientCommandManager.DISPATCHER.register(
				ClientCommandManager.literal("reloadseasons").executes((source) -> {
					BetterFarmingClient.GetBiomeSeasonHandler().Reload(source.getSource().getWorld());
					return 0;
				}));
	}

}
