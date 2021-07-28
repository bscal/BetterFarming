package me.bscal.betterfarming.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.seasons.SeasonManager;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import static com.mojang.brigadier.arguments.IntegerArgumentType.*;
import static net.minecraft.server.command.CommandManager.*;

public class SeasonCommand implements Command<ServerCommandSource>, CommandRegistrationCallback
{

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated)
	{
		dispatcher.register(literal("seasons").then(literal("set").then(argument("season", integer(0, 3)).executes(this))));
	}

	@Override
	public int run(CommandContext<ServerCommandSource> context)
	{
		int season = context.getArgument("season", int.class);
		SeasonManager.GetOrCreate().SetSeason(season);
		return 0;
	}
}
