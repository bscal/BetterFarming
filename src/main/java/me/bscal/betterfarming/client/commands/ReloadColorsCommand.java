package me.bscal.betterfarming.client.commands;

import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

public final class ReloadColorsCommand
{

	public static void Register(BiomeSeasonHandler handler)
	{
		ClientCommandManager.DISPATCHER.register(
				ClientCommandManager.literal("reloadseasons").executes((source) -> {
					handler.Reload(source.getSource().getWorld());
					return 0;
				}));
	}

}
