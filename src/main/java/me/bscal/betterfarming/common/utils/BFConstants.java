package me.bscal.betterfarming.common.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.Tag;

import java.util.HashSet;

public final class BFConstants
{

	public static final IntProperty SPREAD_AMOUNT;

	public static final Tag<Block> FARM_FOOD = Tag.of(new HashSet<>()
	{{
		add(Blocks.GRASS_BLOCK);
		add(Blocks.GRASS);
		add(Blocks.TALL_GRASS);
		add(Blocks.HAY_BLOCK);
	}});

	static
	{
		SPREAD_AMOUNT = IntProperty.of("spread", 0, 255);
	}
}
