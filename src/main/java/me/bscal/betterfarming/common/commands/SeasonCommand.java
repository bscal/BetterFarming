package me.bscal.betterfarming.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.bscal.betterfarming.BetterFarming;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.IntegerArgumentType.*;
import static net.minecraft.server.command.CommandManager.*;

public class SeasonCommand implements Command<ServerCommandSource>, CommandRegistrationCallback
{

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated)
	{
		if (dedicated)
		{
			dispatcher.register(literal("seasons").then(literal("set").then(argument("season", integer()).executes(this))));
		}
	}

	@Override
	public int run(CommandContext<ServerCommandSource> context)
	{
		int season = context.getArgument("season", int.class);
		BetterFarming.LOGGER.info("HELLLLOOOO " + season);

		return 0;
	}
}
