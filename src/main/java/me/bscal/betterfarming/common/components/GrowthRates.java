package me.bscal.betterfarming.common.components;

import net.minecraft.nbt.NbtCompound;

public class GrowthRates
{

	public int crops = 50;
	public int trees = 50;
	public int grass = 50;

	public void FromTag(NbtCompound tag)
	{
		crops = tag.getInt("crops");
		trees = tag.getInt("trees");
		grass = tag.getInt("grass");
	}

	public void ToTag(NbtCompound tag)
	{
		tag.putInt("crops", crops);
		tag.putInt("trees", trees);
		tag.putInt("grass", grass);
	}

}
