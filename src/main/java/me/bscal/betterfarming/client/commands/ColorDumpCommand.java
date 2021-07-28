package me.bscal.betterfarming.client.commands;

import com.mojang.brigadier.context.CommandContext;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import me.bscal.betterfarming.common.utils.Color;
import me.bscal.betterfarming.mixin.client.biome.BiomeInvoker;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.io.FileWriter;
import java.io.IOException;

public final class ColorDumpCommand
{

	public static void Register(BiomeSeasonHandler handler)
	{
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
					handler.biomeEffectChangerMap.forEach((biome, changer) -> {
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

		ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("seasoncolors_dump")
				.executes((source) -> DumpSeasonalColors(source, handler,
						FabricLoader.getInstance().getConfigDir() + "\\season_colors_dump.txt")));
	}

	public static int DumpSeasonalColors(CommandContext<FabricClientCommandSource> source,
			BiomeSeasonHandler handler, String path)
	{
		try
		{
			FileWriter writer = new FileWriter(path);
			source.getSource().getWorld().getRegistryManager().get(Registry.BIOME_KEY).forEach(key -> {
				try
				{
					writer.append(String.valueOf(source.getSource()
							.getWorld()
							.getRegistryManager()
							.get(Registry.BIOME_KEY)
							.getId(key)))
							.append(" ------- ")
							.append(new Color(key.getGrassColorAt(0, 0)).toHex())
							.append('\n');

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			});
			handler.biomeEffectChangerMap.forEach((biome, changer) -> {
				String str = String.format("%s colors : D=%s | S=%s,%s,%s,%s", changer.key,
						new Color(((BiomeInvoker) (Object) biome).invokeGetDefaultGrassColor()).toHex(),
						new Color(changer.grassColors[0]).toHex(), new Color(changer.grassColors[1]).toHex(),
						new Color(changer.grassColors[2]).toHex(), new Color(changer.grassColors[3]).toHex());
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
