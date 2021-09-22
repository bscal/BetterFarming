package me.bscal.betterfarming.common.database.blockdataV2;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CompoundDataBlock implements IDataBlock
{

	public NbtCompound root;

	@Override
	public Block GetBlock()
	{
		if (!root.contains("blockId"))
			return null;
		return Registry.BLOCK.get(new Identifier(root.getString("blockId")));
	}

	@Override
	public NbtCompound AsNbt()
	{
		return root;
	}

	public static IDataBlock NewInstance(NbtCompound nbt)
	{
		CompoundDataBlock instance = new CompoundDataBlock();
		instance.root = nbt;
		return instance;
	}
}
