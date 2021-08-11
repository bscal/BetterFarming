package me.bscal.betterfarming.common.database.blockdata.blocks;

import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import net.minecraft.nbt.NbtCompound;

public class TestDataBlock implements IBlockDataBlock
{

	public int i = 5;

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		nbt.putInt("i", i);
		return nbt;
	}

	@Override
	public void FromNbt(NbtCompound nbt)
	{
		i = nbt.getInt("i");
	}
}
