package me.bscal.betterfarming.common.database.blockdata.smart;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.function.Supplier;

public class SmartDataChunk implements IBlockDataChunk
{

	private Int2ObjectOpenHashMap<SmartDataChunkSection> m_blockData;

	public SmartDataChunk()
	{
		m_blockData = new Int2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
	}

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		NbtList list = new NbtList();
		for (var pair : m_blockData.int2ObjectEntrySet())
		{
			NbtCompound entry = new NbtCompound();
			entry.putInt("y", pair.getIntKey());
			pair.getValue().ToNbt(entry);
			list.add(entry);
		}
		nbt.put("sections", list);
		return nbt;
	}

	@Override
	public SmartDataChunk FromNbt(NbtCompound nbt)
	{
		SmartDataChunk chunk = new SmartDataChunk();
		NbtList list = nbt.getList("data", NbtElement.COMPOUND_TYPE);
		chunk.m_blockData = new Int2ObjectOpenHashMap<>(list.size() + 8, 1f);
		list.forEach((element -> {
			if (element instanceof NbtCompound compound)
			{
				chunk.m_blockData.put(compound.getInt("y"), new SmartDataChunkSection(compound));
			}
		}));
		return chunk;
	}

	@Override
	public IBlockDataBlock GetBlock(int x, int y, int z)
	{
		return m_blockData.get(y).GetBlockData(x, z);
	}

	@Override
	public void PutBlock(int x, int y, int z, IBlockDataBlock data)
	{
		m_blockData.get(y).SetBlockData(x, z, data);
	}

	@Override
	public IBlockDataBlock RemoveBlock(int x, int y, int z)
	{
		return m_blockData.get(y).RemoveBlock(x, z);
	}

	public SmartDataChunkSection CreateChunk(int y)
	{
		var section = new SmartDataChunkSection();
		m_blockData.put(y, section);
		return section;
	}

	@Override
	public IBlockDataBlock GetOrCreate(int x, int y, int z, Supplier<IBlockDataBlock> blockDataFactory)
	{
		var section = m_blockData.get(y);
		return (section == null) ? CreateChunk(y).GetBlockData(x, z, blockDataFactory) : section.GetBlockData(x, z, blockDataFactory);
	}

	public void CreateAndPut(int x, int y, int z, IBlockDataBlock data)
	{
		var section = m_blockData.get(y);
		if (section == null)
			CreateChunk(y).SetBlockData(x, z, data);
		else
			section.SetBlockData(x, z, data);
	}

}
