package me.bscal.betterfarming.common.database.blockdata.worldpos;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.function.Supplier;

public class WorldPosChunk implements IBlockDataChunk
{

	protected final Long2ObjectOpenHashMap<Long2ObjectOpenHashMap<IBlockDataBlock>> m_blockData;
	protected WorldPosWorld m_parent;

	public WorldPosChunk(WorldPosWorld parent)
	{
		m_parent = parent;
		m_blockData = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
	}

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		NbtList list = new NbtList();
		for (var pair : m_blockData.long2ObjectEntrySet())
		{
			for (var pair2 : pair.getValue().long2ObjectEntrySet())
			{
				NbtCompound entry = new NbtCompound();
				entry.putLong("chunkKey", pair.getLongKey());
				entry.putLong("posKey", pair2.getLongKey());
				pair2.getValue().ToNbt(entry);
				list.add(entry);
			}
		}
		nbt.put("chunks", list);
		return nbt;
	}

	@Override
	public IBlockDataChunk FromNbt(NbtCompound nbt)
	{
		NbtList list = nbt.getList("chunks", NbtElement.COMPOUND_TYPE);
		list.forEach((element -> {
			if (element instanceof NbtCompound compound)
			{
				long chunkKey = compound.getLong("chunkKey");
				Long2ObjectOpenHashMap<IBlockDataBlock> map = m_blockData.get(chunkKey);
				if (map == null)
				{
					map = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
					m_blockData.put(chunkKey, map);
				}

				long posKey = compound.getLong("posKey");
				IBlockDataBlock data = m_parent.parent.m_dataFactory.get();
				data.FromNbt(compound);
				map.put(posKey, data);
			}
		}));
		return this;
	}

	@Override
	public IBlockDataBlock GetBlock(BlockPos pos)
	{
		ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
		var chunks = m_blockData.get(chunkPos.toLong());
		if (chunks == null)
			return null;
		return chunks.get(pos.asLong());
	}

	@Override
	public IBlockDataBlock GetOrCreate(BlockPos pos, Supplier<IBlockDataBlock> blockDataFactory)
	{
		ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
		var chunks = m_blockData.get(chunkPos.toLong());
		if (chunks == null)
		{
			chunks = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
			var data = blockDataFactory.get();
			chunks.put(pos.asLong(), data);
			return data;
		}
		return chunks.get(pos.asLong());
	}

	@Override
	public void PutBlock(BlockPos pos, IBlockDataBlock blockData)
	{
		ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
		var chunks = m_blockData.get(chunkPos.toLong());
		if (chunks == null)
		{
			chunks = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
			chunks.put(pos.asLong(), blockData);
		}
		chunks.put(pos.asLong(), blockData);
	}

	@Override
	public IBlockDataBlock RemoveBlock(BlockPos pos)
	{
		long chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())).toLong();
		var chunks = m_blockData.get(chunkPos);
		if (chunks == null)
			return null;

		var data = chunks.remove(pos.asLong());
		if (chunks.size() < 1)
			m_blockData.remove(chunkPos);
		return data;
	}

	@Override
	public int Size()
	{
		return m_blockData.size();
	}

	public NbtCompound ChunkToNbt(ChunkPos chunkPos, NbtCompound nbt)
	{
		NbtList list = new NbtList();
		long chunkKey = chunkPos.toLong();
		var chunk = m_blockData.get(chunkKey);

		if (chunk == null)
			return null;

		for (var pair : chunk.long2ObjectEntrySet())
		{
			NbtCompound entry = new NbtCompound();
			entry.putLong("chunkKey", chunkKey);
			entry.putLong("posKey", pair.getLongKey());
			pair.getValue().ToNbt(entry);
			list.add(entry);
		}

		nbt.put("chunks", list);
		return nbt;
	}
}
