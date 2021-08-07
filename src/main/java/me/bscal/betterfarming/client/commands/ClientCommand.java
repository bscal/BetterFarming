package me.bscal.betterfarming.client.commands;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ClientCommand
{

	void Register();

}
