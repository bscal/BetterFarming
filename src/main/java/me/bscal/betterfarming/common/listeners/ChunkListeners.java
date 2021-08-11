package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.database.blockdata.BlockDataChunkManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkListeners implements ServerChunkEvents.Load, ServerChunkEvents.Unload
{
	@Override
	public void onChunkLoad(ServerWorld world, WorldChunk chunk)
	{
		BetterFarming.dataChunkManager.OnLoad(world, chunk);
	}

	@Override
	public void onChunkUnload(ServerWorld world, WorldChunk chunk)
	{
		BetterFarming.dataChunkManager.OnUnload(world, chunk);
	}
}
