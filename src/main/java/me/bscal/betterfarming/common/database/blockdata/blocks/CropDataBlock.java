package me.bscal.betterfarming.common.database.blockdata.blocks;

import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CropDataBlock implements IBlockDataBlock
{

	public long lastUpdate;
	public int growthTime;
	public int age;
	public Block block;
	public boolean ableToGrow;

	public CropDataBlock(long lastUpdate, int growthTime, int age, Block block, boolean ableToGrow)
	{
		this.lastUpdate = lastUpdate;
		this.growthTime = growthTime;
		this.age = age;
		this.block = block;
		this.ableToGrow = ableToGrow;
	}

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		nbt.putLong("lastUpdate", lastUpdate);
		nbt.putInt("growthTime", growthTime);
		nbt.putInt("age", age);
		nbt.putString("block", String.valueOf(Registry.BLOCK.getId(block)));
		nbt.putBoolean("ableToGrow", ableToGrow);
		return nbt;
	}

	@Override
	public void FromNbt(NbtCompound nbt)
	{
		lastUpdate = nbt.getLong("lastUpdate");
		growthTime = nbt.getInt("growthTime");
		age = nbt.getInt("age");
		block = Registry.BLOCK.get(Identifier.tryParse(nbt.getString("block")));
		ableToGrow = nbt.getBoolean("ableToGrow");
	}
}
