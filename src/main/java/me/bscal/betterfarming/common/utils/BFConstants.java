package me.bscal.betterfarming.common.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.Tag;

import java.util.HashSet;

public final class BFConstants
{

	/**
	 * Average time it takes for a block to be randomly ticked. 68.27 second
	 */
	public static final double AVERAGE_RANDOM_TICK = 1365.33;
	/**
	 * Median time it takes for a block to be randomly ticked. 47.30 second
	 */
	public static final int MEDIAN_RANDOM_TICK = 946;
	/**
	 * Median time it takes for a block to be randomly ticked. 47.30 second
	 */
	public static final int DAY_TICKS = 24000;
	/**
	 * Median time it takes for a block to be randomly ticked. 47.30 second
	 */
	public static final int REAL_HOUR_TICKS = 72000;
	/**
	 * Median time it takes for a block to be randomly ticked. 47.30 second
	 */
	public static final int REAL_HALFDAY_TICKS = 864000;
	/**
	 * Median time it takes for a block to be randomly ticked. 47.30 second
	 */
	public static final int REAL_DAY_TICKS = 1728000;

	public static final BooleanProperty BOOLEAN_PROPERTY;

	public static final Tag<Block> FARM_FOOD;

	static
	{
		BOOLEAN_PROPERTY = BooleanProperty.of("boolean");

		FARM_FOOD = Tag.of(new HashSet<>()
		{{
			add(Blocks.GRASS_BLOCK);
			add(Blocks.GRASS);
			add(Blocks.TALL_GRASS);
			add(Blocks.HAY_BLOCK);
		}});
	}
}
