package me.bscal.betterfarming.common;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.Tag;

import java.util.HashSet;

public class BFConstants
{

	public static final Tag<Block> FARM_FOOD = Tag.of(new HashSet<>()
	{{
		add(Blocks.GRASS_BLOCK);
		add(Blocks.GRASS);
		add(Blocks.TALL_GRASS);
		add(Blocks.HAY_BLOCK);
	}});
}
