package me.bscal.betterfarming.common.database.blockdata.array;

import me.bscal.betterfarming.common.database.blockdata.DataWorld;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataChunk;
import me.bscal.betterfarming.common.database.blockdata.smart.SmartDataChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.io.File;
import java.io.IOException;

public class ArrayDataWorld extends DataWorld
{
	public ArrayDataWorld(ServerWorld world)
	{
		super(world);
	}

	@Override
	public IBlockDataChunk GetOrCreateChunk(ChunkPos pos)
	{
		var dataChunk = m_chunkToSection.get(pos.toLong());
		return dataChunk == null ? m_chunkToSection.getOrDefault(pos.toLong(), new ArrayDataChunk()) : dataChunk;
	}

	@Override
	public void OnLoadChunk(ServerWorld world, WorldChunk chunk)
	{
		ChunkPos pos = chunk.getPos();
		File file = new File(m_saveDir, ChunkFileName(pos.x, pos.z));
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
