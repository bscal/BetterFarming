package me.bscal.betterfarming.common.database.blockdata;

import net.minecraft.nbt.NbtCompound;

import java.util.function.Supplier;

public interface IBlockDataChunk
{

	NbtCompound ToNbt(NbtCompound nbt);

	IBlockDataChunk FromNbt(NbtCompound nbt);

	IBlockDataBlock GetBlock(int x, int y, int z);

	IBlockDataBlock GetOrCreate(int x, int y, int z, Supplier<IBlockDataBlock> blockDataFactory);

	void PutBlock(int x, int y, int z, IBlockDataBlock blockData);

	IBlockDataBlock RemoveBlock(int x, int y, int z);

}
