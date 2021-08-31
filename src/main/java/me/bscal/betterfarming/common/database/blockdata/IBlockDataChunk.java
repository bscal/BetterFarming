package me.bscal.betterfarming.common.database.blockdata;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

public interface IBlockDataChunk
{

	NbtCompound ToNbt(NbtCompound nbt);

	IBlockDataChunk FromNbt(NbtCompound nbt);

	IBlockDataBlock GetBlock(BlockPos pos);

	IBlockDataBlock GetOrCreate(BlockPos pos, Supplier<IBlockDataBlock> blockDataFactory);

	void PutBlock(BlockPos pos, IBlockDataBlock blockData);

	IBlockDataBlock RemoveBlock(BlockPos pos);

	int Size();

}
