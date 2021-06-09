package me.bscal.betterfarming.common.components.entity;

import me.bscal.betterfarming.common.components.entity.types.AnimalEcoComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class EntityEcoComponent implements IEntityEcoComponent
{

	public static final int MAX_DEFAULT_VALUE = 20;

	public final LivingEntity entity;

	public int growthStage;
	public int happiness;
	public int condition;

	public int fatness;
	public int hunger;
	public int thirst;
	public boolean overcrowded;
	public int ticksForGrowth;
	public int maxGrowth;

	public EntityEcoComponent(LivingEntity entity)
	{
		this.entity = entity;
		if (entity.age < 1)
		{
			growthStage = (entity.isBaby()) ? 0 : 2;
			happiness = 2;
			fatness = 1;
			hunger = 10;
			thirst = 20;
		}
	}

	@Override
	public void serverTick()
	{
	}

	@Override
	public void EatFood(int value)
	{
		AnimalEcoComponent.TryEat(this, value);
	}

	@Override
	public float GetGrowthRate()
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
	public int GetCondition()
	{
		return condition;
	}

	@Override
	public boolean IsOvercrowded()
	{
		return overcrowded;
	}

	@Override
	public int GetFatness()
	{
		return fatness;
	}

	@Override
	public int GetHunger()
	{
		return hunger;
	}

	@Override
	public int GetThirst()
	{
		return thirst;
	}

	@Override
	public void readFromNbt(NbtCompound tag)
	{
		growthStage = tag.getInt("growthStage");
		happiness = tag.getInt("happiness");
		condition = tag.getInt("condition");
		fatness = tag.getInt("fatness");
		hunger = tag.getInt("hunger");
		thirst = tag.getInt("thirst");
		overcrowded = tag.getBoolean("overcrowdedness");
	}

	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putInt("growthStage", growthStage);
		tag.putInt("happiness", happiness);
		tag.putInt("condition", condition);
		tag.putInt("fatness", fatness);
		tag.putInt("hunger", hunger);
		tag.putInt("thirst", thirst);
		tag.putBoolean("overcrowded", overcrowded);
	}
}
