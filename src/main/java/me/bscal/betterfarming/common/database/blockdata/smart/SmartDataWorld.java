package me.bscal.betterfarming.common.database.blockdata.smart;

import me.bscal.betterfarming.common.database.blockdata.DataManager;
import me.bscal.betterfarming.common.database.blockdata.DataWorld;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataChunk;
import me.bscal.betterfarming.common.database.blockdata.worldpos.WorldPosChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.WorldChunk;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class SmartDataWorld extends DataWorld
{
	public SmartDataWorld(String id, ServerWorld world)
	{
		super(id, world);
	}

	@Override
	public IBlockDataChunk GetOrCreateChunk(ChunkPos pos)
	{
		long key = pos.toLong();
		var chunk = m_chunkToSection.get(key);
		if (chunk == null)
		{
			var newChunk = new SmartDataChunk();
			m_chunkToSection.put(key, newChunk);
			return newChunk;
		}
		return chunk;
	}

	@Override
	public IBlockDataBlock Create(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> factory)
	{
		long key = pos.asLong();
		IBlockDataChunk chunk = m_chunkToSection.get(ChunkSectionPos.from(pos).asLong());
		if (chunk == null)
		{
			var newChunk = new SmartDataChunk();
			m_chunkToSection.put(key, newChunk);
		}
		var data = factory.get();
		chunk.PutBlock(pos, data);
		return data;
	}


	@Override
	public void OnLoadChunk(ServerWorld world, WorldChunk chunk)
	{
		ChunkPos pos = chunk.getPos();
		File file = new File(m_saveDir, DataManager.ChunkFileName(pos.x, pos.z));
		if (file.exists())
		{
			try
			{
				NbtCompound nbt = NbtIo.readCompressed(file);
				m_chunkToSection.put(pos.toLong(), new SmartDataChunk().FromNbt(nbt));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
