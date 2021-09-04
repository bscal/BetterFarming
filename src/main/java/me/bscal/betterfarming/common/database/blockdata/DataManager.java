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

		IBlockDataBlock dataBlock = dataChunk.GetBlock(pos);
		return dataBlock == null ? EmptyDataBlock.EMPTY_DATA : dataBlock;
	}

	@Override
	public IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos)
	{
		return GetWorld(world).GetOrCreateChunk(world.getChunk(pos).getPos())
				.GetOrCreate(pos, blockDataFactoryDefault);
	}

	@Override
	public IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> customBlockDataFactor)
	{
		return GetWorld(world).GetOrCreateChunk(world.getChunk(pos).getPos())
				.GetOrCreate(pos, customBlockDataFactor);
	}

	@Override
	public IBlockDataBlock Create(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> factory)
	{
		return GetWorld(world).Create(world, pos, factory);
	}

	@Override
	public void RemoveBlockData(ServerWorld world, BlockPos pos)
	{
		GetWorld(world).Remove(pos);
	}

	@Override
	public void SetBlockData(ServerWorld world, BlockPos pos, IBlockDataBlock data)
	{
		GetWorld(world).GetOrCreateChunk(world.getChunk(pos).getPos()).PutBlock(pos, data);
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
			if (dataWorld.GetServerWorld().getRegistryKey().getValue().equals(world.getRegistryKey().getValue()))
				return dataWorld;
		}
		return null;
	}

	@Override
	public IBlockDataBlock[] GetAll(ServerWorld world)
	{
		return GetWorld(world).GetAll(world);
	}

	@Override
	public IBlockDataBlock[] GetAllChunk(ServerWorld world, ChunkPos pos)
	{
		return GetWorld(world).GetAllChunk(world, pos);
	}

	@Override
	public void OnLoadChunk(ServerWorld world, WorldChunk chunk)
	{
		for (var worldData : worlds)
		{
			if (worldData.GetServerWorld().equals(world))
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
			if (worldData.GetServerWorld().equals(world))
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
		return ((long)x << 32) | z & 0xffL;
	}

	public static int[] LongToXZ(long xz) {
		int[] i = new int[2];
		i[0] = (int) (xz >> 32);
		i[1] = (int) xz;
		return i;
	}

	public static String ChunkFileName(int x, int z)
	{
		return x + "-" + z + ".dat";
	}

}
