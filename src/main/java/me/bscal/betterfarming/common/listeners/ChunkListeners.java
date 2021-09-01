package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.BetterFarming;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkListeners implements ServerChunkEvents.Load, ServerChunkEvents.Unload
{
	@Override
	public void onChunkLoad(ServerWorld world, WorldChunk chunk)
	{
		BetterFarming.WORLD_DATAMANGER.OnLoadChunk(world, chunk);
	}

	@Override
	public void onChunkUnload(ServerWorld world, WorldChunk chunk)
	{
		BetterFarming.WORLD_DATAMANGER.OnUnloadChunk(world, chunk);
	}
}
