package me.bscal.betterfarming.common.database.blockdata;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.function.Supplier;

public interface IBlockDataManager
{

	boolean IsPersistent();

	String GetId();

	void SetPersistent(boolean persistent);

	IBlockDataWorld GetWorld(ServerWorld world);

	IBlockDataWorld SetupWorld(ServerWorld world);

	IBlockDataBlock GetBlockData(ServerWorld world, BlockPos pos);

	IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos);

	IBlockDataBlock GetOrCreateBlockData(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> customBlockDataFactor);

	IBlockDataBlock Create(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> factory);

	void SetBlockData(ServerWorld world, BlockPos pos, IBlockDataBlock data);

	void RemoveBlockData(ServerWorld world, BlockPos pos);

	boolean DoesChunkExist(ServerWorld world, ChunkPos pos);

	IBlockDataBlock[] GetAll(ServerWorld world);

	IBlockDataBlock[] GetAllChunk(ServerWorld world, ChunkPos pos);

	Supplier<IBlockDataBlock> GetDataBlockFactory();

	void OnLoadChunk(ServerWorld world, WorldChunk chunk);

	void OnUnloadChunk(ServerWorld world, WorldChunk chunk);

	void Save(ServerWorld world);

	void Load(ServerWorld world);

}
