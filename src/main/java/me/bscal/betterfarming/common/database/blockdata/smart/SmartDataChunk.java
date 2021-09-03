package me.bscal.betterfarming.common.database.blockdata.smart;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.bscal.betterfarming.common.database.blockdata.DataManager;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataChunk;
import me.bscal.betterfarming.common.utils.LongPair;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
	public IBlockDataChunk FromNbt(NbtCompound nbt)
	{
		NbtList list = nbt.getList("sections", NbtElement.COMPOUND_TYPE);
		this.m_blockData = new Int2ObjectOpenHashMap<>(list.size() + 8, 1f);
		list.forEach((element -> {
			if (element instanceof NbtCompound compound)
			{
				this.m_blockData.put(compound.getInt("y"), new SmartDataChunkSection(compound));
			}
		}));
		return this;
	}

	@Override
	public IBlockDataBlock GetBlock(BlockPos pos)
	{
		return m_blockData.get(pos.getY()).GetBlockData(pos.getX(), pos.getZ());
	}

	@Override
	public void PutBlock(BlockPos pos, IBlockDataBlock data)
	{
		m_blockData.get(pos.getY()).SetBlockData(pos.getX(), pos.getZ(), data);
	}

	@Override
	public IBlockDataBlock RemoveBlock(BlockPos pos)
	{
		return m_blockData.get(pos.getY()).RemoveBlock(pos.getX(), pos.getZ());
	}

	@Override
	public IBlockDataBlock[] GetAll(ServerWorld world)
	{
		Stream<IBlockDataBlock> stream = Stream.of();
		for (var chunkSection : m_blockData.values())
			stream = Stream.concat(stream, chunkSection.GetMap().values().stream());
		return (IBlockDataBlock[]) stream.toArray();
	}

	@Override
	public Object GetMap()
	{
		return m_blockData;
	}

	@Override
	public void ForEach(Consumer<IBlockDataBlock> foreach)
	{
		m_blockData.forEach((integer, smartDataChunkSection) -> {
			smartDataChunkSection.GetMap().values().forEach(foreach);
		});
	}

	@Override
	public int Size()
	{
		return m_blockData.size();
	}

	public SmartDataChunkSection CreateChunk(int y)
	{
		var section = new SmartDataChunkSection();
		m_blockData.put(y, section);
		return section;
	}

	@Override
	public IBlockDataBlock GetOrCreate(BlockPos pos, Supplier<IBlockDataBlock> blockDataFactory)
	{
		var section = m_blockData.get(pos.getY());
		return (section == null) ?
				CreateChunk(pos.getY()).GetBlockData(pos.getX(), pos.getZ(), blockDataFactory) :
				section.GetBlockData(pos.getX(), pos.getZ(), blockDataFactory);
	}

	public void CreateAndPut(BlockPos pos, IBlockDataBlock data)
	{
		var section = m_blockData.get(pos.getY());
		if (section == null)
			CreateChunk(pos.getY()).SetBlockData(pos.getX(), pos.getZ(), data);
		else
			section.SetBlockData(pos.getX(), pos.getZ(), data);
	}

}
