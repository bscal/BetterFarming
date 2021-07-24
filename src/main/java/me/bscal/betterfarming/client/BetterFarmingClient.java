package me.bscal.betterfarming.client;

import com.mojang.brigadier.context.CommandContext;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import me.bscal.betterfarming.common.events.ClientWorldEvent;
import me.bscal.betterfarming.common.utils.Color;
import me.bscal.betterfarming.mixin.client.biome.BiomeInvoker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Environment(EnvType.CLIENT) public class BetterFarmingClient implements ClientModInitializer
{

	private static final BiomeSeasonHandler m_seasonHandler = new BiomeSeasonHandler();

	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(BetterFarming.SYNC_PACKET,
				BiomeSeasonHandler.SyncTimeS2CPacketHandler());

		ClientWorldEvent.CLIENT_JOIN_WORLD_EVENT.register(
				((client, world) -> m_seasonHandler.RegisterBiomeChangers(world)));

		ClientTickEvents.END_WORLD_TICK.register((world -> {
			if (!m_seasonHandler.recievedSyncPacket) // Instead of sending packet every we tick we can simulate time passing.
				m_seasonHandler.seasonClock.ticksSinceCreation++;
		}));

		ClientCommandManager.DISPATCHER.register(
				ClientCommandManager.literal("seasoncolors").executes((source) -> {
					source.getSource()
							.getWorld()
							.getRegistryManager()
							.get(Registry.BIOME_KEY)
							.forEach(key -> MinecraftClient.getInstance().inGameHud.getChatHud()
									.addMessage(Text.of(source.getSource()
											.getWorld()
											.getRegistryManager()
											.get(Registry.BIOME_KEY)
											.getId(key) + " ------- " + new Color(
											key.getGrassColorAt(0, 0)).toHex())));
					m_seasonHandler.biomeEffectChangerMap.forEach((biome, changer) -> {
						String str = String.format("%s colors : D=%s | S=%s,%s,%s,%s", changer.key, new Color(
										((BiomeInvoker) (Object) biome).invokeGetDefaultGrassColor()).toHex(),
								new Color(changer.grassColors[0]).toHex(),
								new Color(changer.grassColors[1]).toHex(),
								new Color(changer.grassColors[2]).toHex(),
								new Color(changer.grassColors[3]).toHex());
						MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(str));
					});
					return 0;
				}));

		ClientCommandManager.DISPATCHER.register(
				ClientCommandManager.literal("seasoncolors_dump").executes((source) -> DumpSeasonalColors(source,
						FabricLoader.getInstance().getConfigDir() + "\\season_colors_dump.txt")));
	}

	public static int GetSeason()
	{
		return m_seasonHandler.seasonClock.currentSeason;
	}

	public static BiomeSeasonHandler GetBiomeSeasonHandler()
	{
		return m_seasonHandler;
	}

	public static int DumpSeasonalColors(CommandContext<FabricClientCommandSource> source, String path)
	{
		try
		{
			FileWriter writer = new FileWriter(path);
			source.getSource()
					.getWorld()
					.getRegistryManager()
					.get(Registry.BIOME_KEY)
					.forEach(key -> {
						try
						{
							writer.append(String.valueOf(source.getSource()
									.getWorld()
									.getRegistryManager()
									.get(Registry.BIOME_KEY)
									.getId(key)))
									.append(" ------- ")
									.append(new Color(key.getGrassColorAt(0, 0)).toHex()).append('\n');

						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					});
			m_seasonHandler.biomeEffectChangerMap.forEach((biome, changer) -> {
				String str = String.format("%s colors : D=%s | S=%s,%s,%s,%s", changer.key, new Color(
								((BiomeInvoker) (Object) biome).invokeGetDefaultGrassColor()).toHex(),
						new Color(changer.grassColors[0]).toHex(),
						new Color(changer.grassColors[1]).toHex(),
						new Color(changer.grassColors[2]).toHex(),
						new Color(changer.grassColors[3]).toHex());
				try
				{
					writer.append(str).append('\n');
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			});
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
}
