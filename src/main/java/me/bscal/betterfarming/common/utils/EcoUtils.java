package me.bscal.betterfarming.common.utils;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EcoUtils
{

	public static boolean Chance(int chance)
	{
		return BetterFarming.RAND.nextInt(100) < chance;
	}

	public static void RandomElementToAir(World world, List<BlockPos> blocks)
	{
		world.setBlockState(blocks.get(BetterFarming.RAND.nextInt(blocks.size())),
				Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
	}

}
