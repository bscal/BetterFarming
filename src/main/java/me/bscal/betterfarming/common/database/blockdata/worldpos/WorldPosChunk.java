package me.bscal.betterfarming.common.database.blockdata.worldpos;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class WorldPosChunk implements IBlockDataChunk
{

	protected final Long2ObjectOpenHashMap<IBlockDataBlock> m_blockData;
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
			NbtCompound entry = new NbtCompound();
			entry.putLong("posKey", pair.getLongKey());
			pair.getValue().ToNbt(entry);
			list.add(entry);
		}
		nbt.put("blocks", list);
		return nbt;
	}

	@Override
	public IBlockDataChunk FromNbt(NbtCompound nbt)
	{
		NbtList list = nbt.getList("blocks", NbtElement.COMPOUND_TYPE);
		list.forEach((element -> {
			if (element instanceof NbtCompound compound)
			{
				long posKey = compound.getLong("posKey");
				IBlockDataBlock data = m_parent.parent.m_dataFactory.get();
				data.FromNbt(compound);
				m_blockData.put(posKey, data);
			}
		}));
		return this;
	}

	@Override
	public IBlockDataBlock GetBlock(BlockPos pos)
	{
		return m_blockData.get(pos.asLong());
	}

	@Override
	public IBlockDataBlock GetOrCreate(BlockPos pos, Supplier<IBlockDataBlock> blockDataFactory)
	{
		long posKey = pos.asLong();
		var data = m_blockData.get(posKey);
		if (data == null)
		{
			data = blockDataFactory.get();
			m_blockData.put(posKey, data);
		}
		return data;
	}

	@Override
	public void PutBlock(BlockPos pos, IBlockDataBlock blockData)
	{
		m_blockData.put(pos.asLong(), blockData);
	}

	@Override
	public IBlockDataBlock RemoveBlock(BlockPos pos)
	{
		return m_blockData.remove(pos.asLong());
	}

	@Override
	public IBlockDataBlock[] GetAll(ServerWorld world)
	{
		return (IBlockDataBlock[]) m_blockData.values().toArray();
	}

	@Override
	public Object GetMap()
	{
		return m_blockData;
	}

	@Override
	public void ForEach(Consumer<IBlockDataBlock> foreach)
	{
		m_blockData.values().forEach(foreach);
	}

	public Long2ObjectMap.FastEntrySet<IBlockDataBlock> GetPairs()
	{
		return m_blockData.long2ObjectEntrySet();
	}

	@Override
	public int Size()
	{
		return m_blockData.size();
	}
}
