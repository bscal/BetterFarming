package me.bscal.betterfarming.common.components.entity.types;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.components.entity.EntityEcoComponent;
import me.bscal.betterfarming.common.utils.EcoUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AnimalEcoComponent extends EntityEcoComponent
{
	public static final Tag<Block> FARM_FOOD = Tag.of(new HashSet<>()
	{{
		add(Blocks.GRASS_BLOCK);
		add(Blocks.GRASS);
		add(Blocks.TALL_GRASS);
		add(Blocks.HAY_BLOCK);
	}});

	private final AnimalEntity animal;

	private int m_internalTimer;

	public AnimalEcoComponent(LivingEntity entity)
	{
		super(entity);
		animal = (AnimalEntity) entity;
	}

	@Override
	public void serverTick()
	{
		// canImmediatelyDespawn(d) implementations don't generally use distanceSquared. (AnimalEntity is always false)
		// I check these because a random animal entity does not really need to be updated.
		if (m_internalTimer++ % BetterFarming.UPDATE_DELAY == 0)
		{
			hunger -= 1;
			thirst -= 1;
			if (growthStage > maxGrowth && animal.age > ticksForGrowth)
				growthStage++;

			Box box = Box.of(animal.getPos(), 8, 8, 8);
			List<AnimalEntity> list = animal.world.getEntitiesByClass(AnimalEntity.class, box,
					(animalClass) -> true);

			overcrowded = list.size() > 7;

			List<BlockPos> hayBlocks = new ArrayList<>();
			List<BlockPos> grassBlocks = BlockPos.stream(box).filter((blockPos) -> {
				BlockState state = animal.world.getBlockState(blockPos);
				if (state.isOf(Blocks.HAY_BLOCK))
					hayBlocks.add(blockPos);
				return state.isIn(FARM_FOOD);
			}).toList();

			if (hunger < 90)
			{
				if (hayBlocks.size() > 0)
				{
					TryEat(this, 25);
					EcoUtils.RandomElementToAir(animal.world, hayBlocks);
				}
				else if (grassBlocks.size() > 0)
				{
					TryEat(this, 15);
					EcoUtils.RandomElementToAir(animal.world, grassBlocks);
				}
			}

		}
	}

	public static void TryEat(AnimalEcoComponent component, int foodValue)
	{
		component.hunger += foodValue;
		component.happiness++;
		if (component.hunger > 100 && BetterFarming.RAND.nextInt(100) < component.hunger - 100)
			component.fatness++;
	}

}
