package me.bscal.betterfarming.common.database.blockdata;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.bscal.betterfarming.common.utils.LongPair;
import me.bscal.betterfarming.common.utils.Pair;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.List;
import java.util.function.Consumer;

public interface IBlockDataWorld
{

	ServerWorld GetServerWorld();

	IBlockDataChunk GetOrCreateChunk(ChunkPos pos);

	IBlockDataChunk Get(ChunkPos pos);

	void Remove(BlockPos pos);

	void OnLoadChunk(ServerWorld world, WorldChunk chunk);

	void OnUnloadChunk(ServerWorld world, WorldChunk chunk);

	IBlockDataBlock[] GetAll(ServerWorld world);

	IBlockDataBlock[] GetAllChunk(ServerWorld world, ChunkPos pos);

	void ForEach(Consumer<IBlockDataBlock> foreach);

	void Save();

}
