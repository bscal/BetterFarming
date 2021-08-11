package me.bscal.betterfarming.common.database.blockdata;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public interface IBlockDataWorld
{

	ServerWorld GetWorld();

	IBlockDataChunk GetOrCreateChunk(ChunkPos pos);

	IBlockDataChunk Get(ChunkPos pos);

	void OnLoadChunk(ServerWorld world, WorldChunk chunk);

	void OnUnloadChunk(ServerWorld world, WorldChunk chunk);

	void Save();

}
