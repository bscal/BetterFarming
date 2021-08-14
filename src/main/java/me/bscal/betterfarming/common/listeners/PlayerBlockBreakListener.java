package me.bscal.betterfarming.common.listeners;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.components.chunk.ChunkEcoProvider;
import me.bscal.betterfarming.common.components.chunk.IChunkEcoComponent;
import me.bscal.betterfarming.common.database.blockdata.blocks.TestDataBlock;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

			TestDataBlock data = (TestDataBlock) BetterFarming.SMART_DATA_MANAGER.GetOrCreateBlockData((ServerWorld) world, pos);
			data.i = 10;

			TestDataBlock data1 = (TestDataBlock) BetterFarming.SMART_DATA_MANAGER.GetOrCreateBlockData((ServerWorld) world, pos.up());
			data1.i = 67;
		}
	}
}
