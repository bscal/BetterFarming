package me.bscal.betterfarming.common.database.blockdata.blocks;

import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CompoundDataBlock implements IBlockDataBlock
{

	public final Block block;
	public NbtCompound data;

	public CompoundDataBlock(Block block)
	{
		this.block = block;
		this.data = new NbtCompound();
	}

	public CompoundDataBlock(Block block, NbtCompound data)
	{
		this.block = block;
		this.data = data.copy();
	}

	public NbtCompound CloneData()
	{
		return data.copy();
	}

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		nbt.putString("block", Registry.BLOCK.getId(block).toString());
		nbt.put("data", data);
		return nbt;
	}

	@Override
	public void FromNbt(NbtCompound nbt)
	{
		data = nbt.getCompound("data");
	}

	public static CompoundDataBlock Create(NbtCompound nbt)
	{
		return new CompoundDataBlock(Registry.BLOCK.get(new Identifier(nbt.getString("block"))), nbt.getCompound("data"));
	}

}
