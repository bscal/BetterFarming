package me.bscal.betterfarming.common.database.blockdataV2;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;

public interface IDataBlock
{

	Block GetBlock();

	NbtCompound AsNbt();

}
