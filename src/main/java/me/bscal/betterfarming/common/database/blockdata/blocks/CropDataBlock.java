package me.bscal.betterfarming.common.database.blockdata.blocks;

import me.bscal.betterfarming.common.database.blockdata.IBlockDataBlock;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CropDataBlock implements IBlockDataBlock
{

	public Block block;
	public float totalGrowthReceived;
	public float currentAgeGrowthReceived;
	public int age;
	public float growthModifier;
	public boolean ableToGrow;

	public CropDataBlock() {}

	public CropDataBlock(Block block)
	{
		this(block, 0, 0, 0);
	}

	public CropDataBlock(Block block, float totalGrowthReceived, float currentAgeGrowthReceived, int age)
	{
		this.block = block;
		this.totalGrowthReceived = totalGrowthReceived;
		this.currentAgeGrowthReceived = currentAgeGrowthReceived;
		this.age = age;
		this.growthModifier = 1.0f;
	}

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		nbt.putString("block", String.valueOf(Registry.BLOCK.getId(block)));
		nbt.putFloat("totalGrowthReceived", totalGrowthReceived);
		nbt.putFloat("currentAgeGrowthReceived", currentAgeGrowthReceived);
		nbt.putInt("age", age);
		nbt.putFloat("growthModifier", growthModifier);
		nbt.putBoolean("ableToGrow", ableToGrow);
		return nbt;
	}

	@Override
	public void FromNbt(NbtCompound nbt)
	{
		String blockId = nbt.getString("block");
		if (blockId.isBlank() || blockId.equals("null"))
			return;
		block = Registry.BLOCK.get(new Identifier(blockId));
		totalGrowthReceived = nbt.getFloat("totalGrowthReceived");
		currentAgeGrowthReceived = nbt.getFloat("currentAgeGrowthReceived");
		age = nbt.getInt("age");
		growthModifier = nbt.getFloat("growthModifier");
		ableToGrow = nbt.getBoolean("ableToGrow");
	}

	@Override
	public Block GetBlock()
	{
		return block;
	}

	public static CropDataBlock Create(NbtCompound nbt)
	{
		CropDataBlock crop = new CropDataBlock();
		crop.FromNbt(nbt);
		return crop;
	}
}
