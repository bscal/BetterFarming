package me.bscal.betterfarming.common.database.blockdata;

import net.minecraft.nbt.NbtCompound;

public class PersistentBlockData
{

	int i;

	public PersistentBlockData(){}

	public PersistentBlockData(int i)
	{
		this.i = i;
	}

	public PersistentBlockData(NbtCompound nbt)
	{
		i = nbt.getInt("i");
	}

	public NbtCompound ToNbt(NbtCompound nbt)
	{
		nbt.putInt("i", i);
		return nbt;
	}


}
