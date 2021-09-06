package me.bscal.betterfarming.common.database.blockdata;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;

public interface IBlockDataBlock
{
	NbtCompound ToNbt(NbtCompound nbt);

	void FromNbt(NbtCompound nbt);

	Block GetBlock();

}
