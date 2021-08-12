package me.bscal.betterfarming.common.database.blockdata.array;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataChunk;
import me.bscal.betterfarming.common.database.blockdata.blocks.TestDataBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.function.Supplier;

public class ArrayDataChunk implements IBlockDataChunk
{
	private final Int2ObjectOpenHashMap<IBlockDataBlock[][]> m_blockData;

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
				for (int j = 0; j < pair.getValue()[i].length; j++)
				{
					xzList.add(pair.getValue()[i][j].ToNbt(new NbtCompound()));
				}
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
	public IBlockDataBlock GetBlock(int x, int y, int z)
	{
		return m_blockData.get(y)[x][z];
	}

	@Override
	public void PutBlock(int x, int y, int z, IBlockDataBlock data)
	{
		m_blockData.get(y)[x][z] = data;
	}

	@Override
	public IBlockDataBlock RemoveBlock(int x, int y, int z)
	{
		return m_blockData.get(y)[x][z] = null;
	}

	@Override
	public IBlockDataBlock GetOrCreate(int x, int y, int z, Supplier<IBlockDataBlock> blockDataFactory)
	{
		IBlockDataBlock[][] blockDataArray = m_blockData.get(y);
		return (blockDataArray == null) ?
				CreateChunk(x, y, z, blockDataFactory)[x][z] :
				IfNullCreate(blockDataArray[x][z], blockDataFactory);
	}

	public IBlockDataBlock[][] CreateChunk(int y)
	{
		IBlockDataBlock[][] data2DArray = new IBlockDataBlock[16][16];
		//Arrays.fill(data2DArray, null);
		m_blockData.put(y, data2DArray);
		return data2DArray;
	}

	public IBlockDataBlock[][] CreateChunk(int x, int y, int z, Supplier<IBlockDataBlock> blockDataFactory)
	{
		IBlockDataBlock[][] data2DArray = CreateChunk(y);
		data2DArray[x][z] = blockDataFactory.get();
		return data2DArray;
	}

	public IBlockDataBlock IfNullCreate(IBlockDataBlock currentValue, Supplier<IBlockDataBlock> blockDataFactory)
	{
		return currentValue == null ? blockDataFactory.get() : currentValue;
	}

	public void CreateAndPut(int x, int y, int z, IBlockDataBlock data)
	{
		IBlockDataBlock[][] blockDataArray = m_blockData.get(y);
		if (blockDataArray == null)
			CreateChunk(y)[x][z] = data;
		else
			blockDataArray[x][z] = data;
	}
}
