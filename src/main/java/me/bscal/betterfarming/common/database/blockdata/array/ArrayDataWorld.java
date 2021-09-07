package me.bscal.betterfarming.common.database.blockdata.array;

import me.bscal.betterfarming.common.database.blockdata.*;
import me.bscal.betterfarming.common.database.blockdata.smart.SmartDataChunk;
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

public class ArrayDataWorld extends DataWorld
{
	public ArrayDataWorld(String id, ServerWorld world)
	{
		super(id, world);
	}

	@Override
	public IBlockDataChunk GetOrCreateChunk(ChunkPos pos)
	{
		var dataChunk = m_chunkToSection.get(pos.toLong());
		return dataChunk == null ? m_chunkToSection.getOrDefault(pos.toLong(), new ArrayDataChunk()) : dataChunk;
	}

	@Override
	public IBlockDataBlock Create(ServerWorld world, BlockPos pos, Supplier<IBlockDataBlock> factory)
	{
		long key = pos.asLong();
		IBlockDataChunk chunk = m_chunkToSection.get(new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())).toLong());
		if (chunk == null)
		{
			var newChunk = new ArrayDataChunk();
			m_chunkToSection.put(key, newChunk);
		}
		var data = factory.get();
		chunk.PutBlock(pos, data);
		return data;
	}

	@Override
	public void OnLoadChunk(ServerWorld world, ChunkPos pos)
	{
		File file = new File(m_saveDir, DataManager.ChunkFileName(pos.x, pos.z));
		if (file.exists())
		{
			try
			{
				NbtCompound nbt = NbtIo.readCompressed(file);
				m_chunkToSection.put(pos.toLong(), new ArrayDataChunk().FromNbt(nbt));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
