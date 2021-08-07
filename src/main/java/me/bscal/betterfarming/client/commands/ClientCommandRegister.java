package me.bscal.betterfarming.client.commands;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT) public final class ClientCommandRegister
{

	private ClientCommandRegister()
	{
	}

	public static void Register()
	{
		new ColorDumpCommand().Register();
		new ReloadColorsCommand().Register();
		new BiomeInfoCommand().Register();
	}

}
