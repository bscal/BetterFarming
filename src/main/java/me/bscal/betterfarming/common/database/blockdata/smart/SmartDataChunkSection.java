package me.bscal.betterfarming.common.database.blockdata.smart;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import me.bscal.betterfarming.common.database.blockdata.DataManager;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.blocks.TestDataBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SmartDataChunkSection
{

	private final Long2ObjectOpenHashMap<IBlockDataBlock> m_blockDataMap;

	public SmartDataChunkSection()
	{
		m_blockDataMap = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
	}

	public SmartDataChunkSection(NbtCompound nbt)
	{
		m_blockDataMap = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
		NbtList list = nbt.getList("data", NbtElement.COMPOUND_TYPE);
		list.forEach(compound -> {
			TestDataBlock dataBlock = new TestDataBlock();
			dataBlock.FromNbt((NbtCompound) compound);
			SetBlockData(((NbtCompound)compound).getLong("xz"), dataBlock);
		});
	}

	public NbtCompound ToNbt(NbtCompound nbt)
	{
		NbtList list = new NbtList();
		for (var pair : m_blockDataMap.long2ObjectEntrySet())
		{
			NbtCompound compound = new NbtCompound();
			compound.putLong("xz", pair.getLongKey());
			pair.getValue().ToNbt(compound);
			list.add(compound);
		}
		nbt.put("data", list);
		return nbt;
	}

	public IBlockDataBlock GetBlockData(int x, int z)
	{
		return m_blockDataMap.get(DataManager.XZToLong(x, z));
	}

	public IBlockDataBlock GetBlockData(int x, int z, Supplier<IBlockDataBlock> blockDataFactory)
	{
		long key = DataManager.XZToLong(x, z);
		var blockData  = m_blockDataMap.getOrDefault(key, blockDataFactory.get());
		m_blockDataMap.put(key, blockData);
		return blockData;
	}

	public IBlockDataBlock SetBlockData(long xz, IBlockDataBlock data)
	{
		return m_blockDataMap.put(xz, data);
	}

	public IBlockDataBlock SetBlockData(int x, int z, IBlockDataBlock data)
	{
		return m_blockDataMap.put(DataManager.XZToLong(x, z), data);
	}

	public Long2ObjectOpenHashMap<IBlockDataBlock> GetMap()
	{
		return m_blockDataMap;
	}

	public Long2ObjectRBTreeMap<IBlockDataBlock> GetSortedMap()
	{
		return new Long2ObjectRBTreeMap<>(m_blockDataMap);
	}

	public List<IBlockDataBlock> GetRandomList()
	{
		List<IBlockDataBlock> list = m_blockDataMap.values().stream().toList();
		Collections.shuffle(list);
		return list;
	}

	public IBlockDataBlock RemoveBlock(int x, int z)
	{
		return m_blockDataMap.remove(DataManager.XZToLong(x, z));
	}
}
