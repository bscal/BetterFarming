package me.bscal.betterfarming.client.commands;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

@Environment(EnvType.CLIENT)
public class ReloadColorsCommand implements ClientCommand
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
