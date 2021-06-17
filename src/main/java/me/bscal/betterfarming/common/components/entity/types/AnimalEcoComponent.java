package me.bscal.betterfarming.common.components.entity.types;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.BFConstants;
import me.bscal.betterfarming.common.components.entity.EntityEcoComponent;
import me.bscal.betterfarming.common.utils.EcoUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class AnimalEcoComponent extends EntityEcoComponent
{
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
		super.serverTick();
		if (m_internalTimer++ % BetterFarming.UPDATE_DELAY == 0)
		{
			hunger -= 1;
			thirst -= 1;

			if (true)
				return;
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
				else if (state.isIn(BFConstants.FARM_FOOD))
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

	public static void TryEat(EntityEcoComponent component, int foodValue)
	{
		component.hunger += foodValue;
		component.happiness++;
		if (component.hunger > 100 && BetterFarming.RAND.nextInt(100) < component.hunger - 100)
			component.fatness++;
	}



	@Override
	public int GetConsumablesPriority(BlockPos blockPos, BlockState state)
	{
		Block block = state.getBlock();
		if (block == Blocks.HAY_BLOCK)
			return 0;
		else if (block == Blocks.GRASS || block == Blocks.TALL_GRASS)
			return 1;
		return 2;
	}

}
