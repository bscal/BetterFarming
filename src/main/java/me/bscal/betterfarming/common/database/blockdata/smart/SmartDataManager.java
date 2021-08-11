package me.bscal.betterfarming.common.database.blockdata.smart;

import me.bscal.betterfarming.common.database.blockdata.DataManager;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.function.Supplier;

public class SmartDataManager extends DataManager
{
	public SmartDataManager(MinecraftServer server)
	{
		super(server);
	}

	public SmartDataManager(MinecraftServer server, Supplier<IBlockDataBlock> blockDataFactoryDefault)
	{
		super(server, blockDataFactoryDefault);
	}

	@Override
	public IBlockDataWorld SetupWorld(ServerWorld world)
	{
		var dataWorld = new SmartDataWorld(world);
		worlds.add(dataWorld);
		return dataWorld;
	}

}
