package me.bscal.betterfarming.common.database.blockdata.array;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataChunk;
import me.bscal.betterfarming.common.database.blockdata.blocks.TestDataBlock;
import me.bscal.betterfarming.common.utils.LongPair;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ArrayDataChunk implements IBlockDataChunk
{
	private final Int2ObjectOpenHashMap<IBlockDataBlock[]> m_blockData;

	public ArrayDataChunk()
	{
		m_blockData = new Int2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
	}

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		NbtList yList = new NbtList();
		for (var pair : m_blockData.int2ObjectEntrySet())
		{
			NbtCompound compound = new NbtCompound();
			compound.putInt("y", pair.getIntKey());
			NbtList xzList = new NbtList();
			for (int i = 0; i < pair.getValue().length; i++)
			{
				xzList.add(pair.getValue()[i].ToNbt(new NbtCompound()));
			}
			compound.put("xz-data", xzList);
			yList.add(compound);
		}
		nbt.put("y-data", yList);
		return nbt;
	}

	@Override
	public IBlockDataChunk FromNbt(NbtCompound nbt)
	{
		NbtList yList = nbt.getList("y-data", NbtElement.COMPOUND_TYPE);
		for (NbtElement eleZ : yList)
		{
			if (eleZ instanceof NbtCompound compound)
			{
				int y = compound.getInt("y");
				for (NbtElement eleXY : compound.getList("xz-data", NbtElement.COMPOUND_TYPE))
				{
					int x = compound.getInt("x");
					int z = compound.getInt("z");
					IBlockDataBlock data = new TestDataBlock();
					data.FromNbt(nbt);
					this.CreateAndPut(x, y, z, data);
				}
			}
		}
		return this;
	}

	@Override
	public IBlockDataBlock GetBlock(BlockPos pos)
	{
		return m_blockData.get(pos.getY())[pos.getX() * pos.getZ()];
	}

	@Override
	public void PutBlock(BlockPos pos, IBlockDataBlock data)
	{
		m_blockData.get(pos.getY())[pos.getX() * pos.getZ()] = data;
	}

	@Override
	public IBlockDataBlock RemoveBlock(BlockPos pos)
	{
		return m_blockData.get(pos.getY())[pos.getX() * pos.getZ()] = null;
	}

	@Override
	public IBlockDataBlock[] GetAll(ServerWorld world)
	{
		Stream<IBlockDataBlock> stream = Stream.of();
		for (var xzArray : m_blockData.values())
			stream = Stream.concat(stream, Arrays.stream(xzArray));
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
		m_blockData.values().forEach(blockArray -> {
			for (IBlockDataBlock iBlockDataBlock : blockArray)
			{
				foreach.accept(iBlockDataBlock);
			}
		});
	}

	@Override
	public int Size()
	{
		return m_blockData.size();
	}

	@Override
	public IBlockDataBlock GetOrCreate(BlockPos pos, Supplier<IBlockDataBlock> blockDataFactory)
	{
		IBlockDataBlock[] blockDataArray = m_blockData.get(pos.getY());
		return (blockDataArray == null) ?
				CreateChunk(pos, blockDataFactory)[pos.getX() * pos.getZ()] :
				IfNullCreate(blockDataArray[pos.getX() * pos.getZ()], blockDataFactory);
	}

	public IBlockDataBlock[] CreateChunk(int y)
	{
		IBlockDataBlock[] data2DArray = new IBlockDataBlock[16 * 16];
		//Arrays.fill(data2DArray, null);
		m_blockData.put(y, data2DArray);
		return data2DArray;
	}

	public IBlockDataBlock[] CreateChunk(BlockPos pos, Supplier<IBlockDataBlock> blockDataFactory)
	{
		IBlockDataBlock[] data2DArray = CreateChunk(pos.getY());
		data2DArray[pos.getX() * pos.getZ()] = blockDataFactory.get();
		return data2DArray;
	}

	public IBlockDataBlock IfNullCreate(IBlockDataBlock currentValue, Supplier<IBlockDataBlock> blockDataFactory)
	{
		return currentValue == null ? blockDataFactory.get() : currentValue;
	}

	public void CreateAndPut(int x, int y, int z, IBlockDataBlock data)
	{
		IBlockDataBlock[] blockDataArray = m_blockData.get(y);
		if (blockDataArray == null)
			CreateChunk(y)[x * z] = data;
		else
			blockDataArray[x * z] = data;
	}
}
