package me.bscal.betterfarming.client.commands;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import me.bscal.betterfarming.common.seasons.MinecraftDate;
import me.bscal.betterfarming.common.seasons.Seasons;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class BiomeInfoCommand
{

	public static void Register(BiomeSeasonHandler handler)
	{
		ClientCommandManager.DISPATCHER.register(
				ClientCommandManager.literal("seasoninfo").executes((source) -> {
					Biome biome = source.getSource()
							.getWorld()
							.getBiome(source.getSource().getPlayer().getBlockPos());
					Optional<RegistryKey<Biome>> key = source.getSource()
							.getWorld()
							.getRegistryManager()
							.get(Registry.BIOME_KEY)
							.getKey(biome);
					ChatHud chatHud = source.getSource().getClient().inGameHud.getChatHud();

					if (key.isEmpty())
					{
						chatHud.addMessage(Text.of("Invalid biome"));
						return 1;
					}

					int season = BetterFarmingClient.GetSeason();

					chatHud.addMessage(Text.of("[ Biome Info ]"));
					chatHud.addMessage(Text.of(String.format("Season: %d, %s", season,
							Seasons.GetNameOfSeason(season))));
					chatHud.addMessage(Text.of(String.format("Biome: %d, %s",
							Seasons.GetSeasonForBiome(key.get(), season),
							Seasons.GetNameOfSeasonByBiome(key.get(), season))));
					chatHud.addMessage(
							Text.of("Biome Type: " + ((Seasons.SPECIAL_SEASONS.containsKey(key.get()) ?
									Seasons.SPECIAL_SEASONS.get(key.get()).getClass().getSimpleName() :
									"NULL"))));
					chatHud.addMessage(Text.of("Date: " + new MinecraftDate(
							handler.seasonClock.ticksSinceCreation).AsDate(true)));

					return 0;
				}));
	}

}
