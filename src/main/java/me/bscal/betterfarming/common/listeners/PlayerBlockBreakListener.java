package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.components.chunk.ChunkEcoProvider;
import me.bscal.betterfarming.common.components.chunk.IChunkEcoComponent;
import me.bscal.betterfarming.common.database.blockdata.BlockDataChunkManager;
import me.bscal.betterfarming.common.database.blockdata.PersistentBlockData;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class PlayerBlockBreakListener implements PlayerBlockBreakEvents.After
{
	@Override
	public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity)
	{
		if (!world.isClient())
		{
			Chunk chunk = world.getChunk(pos);
			IChunkEcoComponent component = ChunkEcoProvider.CHUNK_ECO.get(chunk);

			BetterFarming.dataChunkManager.GetOrCreateDataChunk(world.getChunk(pos).getPos())
					.GetSection(player.getBlockY())
					.SetBlockData(pos.getX(), pos.getZ(), new PersistentBlockData(5));


			BetterFarming.dataChunkManager.GetOrCreateDataChunk(world.getChunk(pos).getPos())
					.GetSection(pos.up().getY())
					.SetBlockData(pos.getX(), pos.getZ(), new PersistentBlockData(67));
		}
	}
}
