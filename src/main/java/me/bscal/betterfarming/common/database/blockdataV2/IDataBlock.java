package me.bscal.betterfarming.common.database.blockdataV2;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;

/**
 * IDataBlocks are custom data holders for DataWorld's. You need to implement a default constructor for deserialization to work.
 */
public interface IDataBlock
{

	Block GetBlock();

	NbtCompound AsNbt();

	void FromNbt(NbtCompound nbt);

}
