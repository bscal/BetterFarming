package me.bscal.betterfarming.components.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class EcoEntityComponent implements IEcoEntityComponent
{

	public final LivingEntity entity;
	public int growthStage;
	public int happiness;
	public int hunger;
	public int overcrowdedness;
	public int yield;

	public EcoEntityComponent(LivingEntity ent)
	{
		this.entity = ent;
	}

	@Override
	public int GetGrowthRate()
	{
		return 1;
	}

	@Override
	public int GetGrowthStage()
	{
		return growthStage;
	}

	@Override
	public int GetHappiness()
	{
		return happiness;
	}

	@Override
	public int GetOvercrowdedness()
	{
		return overcrowdedness;
	}

	@Override
	public int GetHunger()
	{
		return hunger;
	}

	@Override
	public int GetYieldGrade()
	{
		return yield;
	}

	@Override
	public void readFromNbt(NbtCompound tag)
	{
		growthStage = tag.getInt("growthStage");
		happiness = tag.getInt("happiness");
		hunger = tag.getInt("hunger");
		overcrowdedness = tag.getInt("overcrowdedness");
		yield = tag.getInt("yield");
	}

	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putInt("growthStage", growthStage);
		tag.putInt("happiness", happiness);
		tag.putInt("hunger", hunger);
		tag.putInt("overcrowdedness", overcrowdedness);
		tag.putInt("yield", yield);
	}
}
