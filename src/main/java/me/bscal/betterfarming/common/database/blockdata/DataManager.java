package me.bscal.betterfarming.common.database.blockdata;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.bscal.betterfarming.common.database.blockdata.blocks.EmptyDataBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.function.Supplier;

public abstract class DataManager implements IBlockDataManager
{

	protected final String id;
	public final ObjectArrayList<IBlockDataWorld> worlds;
	public final Supplier<IBlockDataBlock> blockDataFactoryDefault;

	public DataManager(String id)
	{
		this(id, () -> EmptyDataBlock.EMPTY_DATA);
	}

	public DataManager(String id, Supplier<IBlockDataBlock> blockDataFactoryDefault)
	{
		this.id = id;
		this.worlds = new ObjectArrayList<>(3 + 3);
		this.blockDataFactoryDefault = blockDataFactoryDefault;
	}

	@Override
	public IBlockDataBlock GetBlockData(ServerWorld world, BlockPos pos)
	{
		ChunkPos chunkPos = world.getChunk(pos).getPos();
		IBlockDataWorld dataWorld = GetWorld(world);
		IBlockDataChunk dataChunk = dataWorld.Get(chunkPos);
		if (dataChunk == null)
			return EmptyDataBlock.EMPTY_DATA;

		IBlockDataBlock dataBlock = dataChunk.GetBlock(pos.getX(), pos.getY(), pos.getZ());
		return dataBlock == null ? EmptyDataBlock.EMPTY_DATA : dataBlock;
	}

	@Override
	public IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos)
	{
		return GetWorld(world).GetOrCreateChunk(world.getChunk(pos).getPos())
				.GetOrCreate(pos.getX(), pos.getY(), pos.getZ(), blockDataFactoryDefault);
	}

	@Override
	public IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> customBlockDataFactor)
	{
		return GetWorld(world).GetOrCreateChunk(world.getChunk(pos).getPos())
				.GetOrCreate(pos.getX(), pos.getY(), pos.getZ(), customBlockDataFactor);
	}

	@Override
	public void SetBlockData(ServerWorld world, BlockPos pos, IBlockDataBlock data)
	{
		GetWorld(world).GetOrCreateChunk(world.getChunk(pos).getPos()).PutBlock(pos.getX(), pos.getY(), pos.getZ(), data);
	}

	@Override
	public boolean DoesChunkExist(ServerWorld world, ChunkPos pos)
	{
		return GetWorld(world).Get(pos) == null;
	}

	@Override
	public IBlockDataWorld GetWorld(ServerWorld world)
	{
		for (IBlockDataWorld dataWorld : worlds)
		{
			if (dataWorld.GetWorld().getRegistryKey().getValue().equals(world.getRegistryKey().getValue()))
				return dataWorld;
		}
		return null;
	}

	@Override
	public void OnLoadChunk(ServerWorld world, WorldChunk chunk)
	{
		for (var worldData : worlds)
		{
			if (worldData.GetWorld().equals(world))
			{
				worldData.OnLoadChunk(world, chunk);
				break;
			}
		}
	}

	@Override
	public void OnUnloadChunk(ServerWorld world, WorldChunk chunk)
	{
		for (var worldData : worlds)
		{
			if (worldData.GetWorld().equals(world))
			{
				worldData.OnUnloadChunk(world, chunk);
				break;
			}
		}
	}

	@Override
	public void Save()
	{
		worlds.forEach(IBlockDataWorld::Save);
	}

	public static long XZToLong(int x, int z)
	{
		return x & 0xffL << 32 | z & 0xffL;
	}

	public static String ChunkFileName(int x, int z)
	{
		return x + "-" + z + ".dat";
	}

}
