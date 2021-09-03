package me.bscal.betterfarming.common.database.blockdata.worldpos;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.bscal.betterfarming.common.database.blockdata.DataManager;
import me.bscal.betterfarming.common.database.blockdata.DataWorld;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.utils.LongPair;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldPosWorld extends DataWorld
{
	protected final WorldPosDataManager parent;

	public WorldPosWorld(ServerWorld world, WorldPosDataManager parent)
	{
		super(parent.id, world);
		this.parent = parent;
	}

	public List<LongPair<Long2ObjectMap.FastEntrySet<IBlockDataBlock>>> GetPairs()
	{
		List<LongPair<Long2ObjectMap.FastEntrySet<IBlockDataBlock>>> list = new ArrayList<>();
		for (var entry : m_chunkToSection.long2ObjectEntrySet())
			if (entry.getValue() instanceof WorldPosChunk worldChunk)
			list.add(new LongPair<>(entry.getLongKey(), worldChunk.GetPairs()));
		return list;
	}

	@Override
	public WorldPosChunk GetOrCreateChunk(ChunkPos pos)
	{
		long key = pos.toLong();
		var chunk = m_chunkToSection.get(key);
		if (chunk == null)
		{
			var newChunk = new WorldPosChunk(this);
			m_chunkToSection.put(key, newChunk);
			return newChunk;
		}
		return (WorldPosChunk) chunk;
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
				m_chunkToSection.put(pos.toLong(), new WorldPosChunk(this).FromNbt(nbt));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
