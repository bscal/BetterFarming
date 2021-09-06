package me.bscal.betterfarming.common.database.blockdata.worldpos;

import me.bscal.betterfarming.common.database.blockdata.DataManager;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataManager;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WorldPosDataManager extends DataManager
{

	public WorldPosDataManager(MinecraftServer server, String id, Supplier<IBlockDataBlock> blockDataFactoryDefault)
	{
		super(server, id, blockDataFactoryDefault);
		this.worlds.add(new WorldPosWorld(server.getOverworld(), this));
	}

	public WorldPosWorld GetWorld()
	{
		return (WorldPosWorld) worlds.get(0);
	}

	@Override
	public IBlockDataWorld GetWorld(ServerWorld world)
	{
		for (IBlockDataWorld dataWorld : worlds)
		{
			if (dataWorld.GetServerWorld().getRegistryKey().getValue().equals(world.getRegistryKey().getValue()))
				return dataWorld;
		}
		return SetupWorld(world);
	}

	@Override
	public IBlockDataWorld SetupWorld(ServerWorld world)
	{
		var dataWorld = new WorldPosWorld(world, this);
		this.worlds.add(dataWorld);
		return dataWorld;
	}

	@Override
	public IBlockDataBlock GetBlockData(ServerWorld world, BlockPos pos)
	{
		return GetWorld(world).Get(new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))).GetBlock(pos);
	}

	public IBlockDataBlock Create(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> factory)
	{
		return GetWorld(world).Create(world, pos, factory);
	}

	@Override
	public IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos)
	{
		return GetWorld(world).GetOrCreateChunk(new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())))
				.GetOrCreate(pos, blockDataFactoryDefault);
	}

	@Override
	public IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> customBlockDataFactor)
	{
		return GetWorld(world).GetOrCreateChunk(new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())))
				.GetOrCreate(pos, customBlockDataFactor);
	}

	@Override
	public void SetBlockData(ServerWorld world, BlockPos pos, IBlockDataBlock data)
	{
		GetWorld(world).GetOrCreateChunk(new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())))
				.PutBlock(pos, data);
	}

	@Override
	public void RemoveBlockData(ServerWorld world, BlockPos pos)
	{
		GetWorld(world).Get(new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))).RemoveBlock(pos);
	}

	@Override
	public boolean DoesChunkExist(ServerWorld world, ChunkPos pos)
	{
		return true;
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
		GetWorld(world).OnLoadChunk(world, chunk);
	}

	@Override
	public void OnUnloadChunk(ServerWorld world, WorldChunk chunk)
	{
		GetWorld(world).OnUnloadChunk(world, chunk);
	}

	@Override
	public void Save()
	{
		for (IBlockDataWorld dataWorld : worlds)
		{
			dataWorld.Save();
		}
	}
}
