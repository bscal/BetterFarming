package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.database.blockdata.CropDataBlockHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkListeners implements ServerChunkEvents.Load, ServerChunkEvents.Unload
{
	@Override
	public void onChunkLoad(ServerWorld world, WorldChunk chunk)
	{
		CropDataBlockHandler.GetManager().OnLoadChunk(world, chunk);
	}

	@Override
	public void onChunkUnload(ServerWorld world, WorldChunk chunk)
	{
		CropDataBlockHandler.GetManager().OnUnloadChunk(world, chunk);
	}
}
