package me.bscal.betterfarming.common.database.blockdata.blocks;

import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;

public class EmptyDataBlock implements IBlockDataBlock
{

	public static final IBlockDataBlock EMPTY_DATA = new EmptyDataBlock();

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		return nbt;
	}

	@Override
	public void FromNbt(NbtCompound nbt)
	{
	}

	@Override
	public Block GetBlock()
	{
		return Blocks.AIR;
	}

}
