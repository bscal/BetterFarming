package me.bscal.betterfarming.common.database.blockdata.worldpos;

import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataManager;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.function.Supplier;

public class WorldPosDataManager implements IBlockDataManager
{

	public final String id;

	protected final WorldPosWorld m_dataWorld;
	protected final Supplier<IBlockDataBlock> m_dataFactory;

	public WorldPosDataManager(String id, ServerWorld world, Supplier<IBlockDataBlock> blockDataFactoryDefault)
	{
		this.id = id;
		this.m_dataWorld = new WorldPosWorld(world, this);
		this.m_dataFactory = blockDataFactoryDefault;
	}

	@Override
	public IBlockDataWorld GetWorld(ServerWorld world)
	{
		return m_dataWorld;
	}

	@Override
	public IBlockDataWorld SetupWorld(ServerWorld world)
	{
		return m_dataWorld;
	}

	@Override
	public IBlockDataBlock GetBlockData(ServerWorld world, BlockPos pos)
	{
		return m_dataWorld.Get().GetBlock(pos);
	}

	@Override
	public IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos)
	{
		return m_dataWorld.Get().GetOrCreate(pos, m_dataFactory);
	}

	@Override
	public IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> customBlockDataFactor)
	{
		return m_dataWorld.Get().GetOrCreate(pos, customBlockDataFactor);
	}

	@Override
	public void SetBlockData(ServerWorld world, BlockPos pos, IBlockDataBlock data)
	{
		m_dataWorld.Get().PutBlock(pos, data);
	}

	@Override
	public void RemoveBlockData(ServerWorld world, BlockPos pos)
	{
		m_dataWorld.Get().RemoveBlock(pos);
	}

	@Override
	public boolean DoesChunkExist(ServerWorld world, ChunkPos pos)
	{
		return true;
	}

	@Override
	public void OnLoadChunk(ServerWorld world, WorldChunk chunk)
	{
		m_dataWorld.OnLoadChunk(world, chunk);
	}

	@Override
	public void OnUnloadChunk(ServerWorld world, WorldChunk chunk)
	{
		m_dataWorld.OnUnloadChunk(world, chunk);
	}

	@Override
	public void Save()
	{
		m_dataWorld.Save();
	}
}