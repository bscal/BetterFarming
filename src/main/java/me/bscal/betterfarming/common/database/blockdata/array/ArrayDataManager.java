package me.bscal.betterfarming.common.database.blockdata.array;

import me.bscal.betterfarming.common.database.blockdata.DataManager;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.function.Supplier;

public class ArrayDataManager extends DataManager
{
	public ArrayDataManager(MinecraftServer server)
	{
		super(server);
	}

	public ArrayDataManager(MinecraftServer server, Supplier<IBlockDataBlock> blockDataFactoryDefault)
	{
		super(server, blockDataFactoryDefault);
	}

	@Override
	public IBlockDataWorld SetupWorld(ServerWorld world)
	{
		var dataWorld = new ArrayDataWorld(world);
		worlds.add(dataWorld);
		return dataWorld;
	}

}
