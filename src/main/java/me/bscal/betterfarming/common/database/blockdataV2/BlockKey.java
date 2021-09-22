package me.bscal.betterfarming.common.database.blockdataV2;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public record BlockKey(ServerWorld world, ChunkPos chunkPos, BlockPos blockPos)
{
}
