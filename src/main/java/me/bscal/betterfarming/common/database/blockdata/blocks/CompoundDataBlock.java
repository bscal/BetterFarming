package me.bscal.betterfarming.common.database.blockdata.blocks;

import me.bscal.betterfarming.common.database.blockdata.DataManager;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class CompoundDataBlock implements IBlockDataBlock
{

	public final long xz;
	public NbtCompound data;

	public CompoundDataBlock(long xz)
	{
		this.xz = xz;
		this.data = new NbtCompound();
	}

	public CompoundDataBlock(int x, int z)
	{
		this(DataManager.XZToLong(x, z));
	}

	public NbtCompound CloneData()
	{
		return data.copy();
	}

	public NbtElement GetSafely(String key, int type, NbtElement defaultValue)
	{
		if (data.contains(key, type))
			return data.get(key);
		return defaultValue;
	}

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		nbt.put(Long.toString(xz), data);
		return nbt;
	}

	@Override
	public void FromNbt(NbtCompound nbt)
	{
		data = nbt.getCompound(Long.toString(xz));
	}
}
