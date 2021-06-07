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
		if (m_internalTimer++ % BetterFarming.UPDATE_DELAY == 0)
		{
			hunger -= 1;
			thirst -= 1;
			if (growthStage > maxGrowth && animal.age > ticksForGrowth)
				growthStage++;

			Box box = Box.from(animal.getPos()).expand(8);
			List<AnimalEntity> list = animal.world.getEntitiesByClass(AnimalEntity.class, box,
					(animalClass) -> true);

			overcrowded = list.size() > 7;

			List<BlockPos> hayBlocks = new ArrayList<>();
			List<BlockPos> grassBlocks = new ArrayList<>();
			BlockPos.stream(box).forEach((blockPos) -> {
				BlockState state = animal.world.getBlockState(blockPos);
				if (state.isOf(Blocks.HAY_BLOCK))
					hayBlocks.add(blockPos.toImmutable());
				else if (state.isIn(FARM_FOOD))
					grassBlocks.add(blockPos.toImmutable());
			});

			if (hunger < 90)
			{
				if (hayBlocks.size() > 0)
				{
					TryEat(this, 25);
					EcoUtils.RandomElementSetToAir(animal.world, hayBlocks);
				}
				else if (grassBlocks.size() > 0)
				{
					TryEat(this, 15);
					EcoUtils.RandomElementSetToAir(animal.world, grassBlocks);
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
