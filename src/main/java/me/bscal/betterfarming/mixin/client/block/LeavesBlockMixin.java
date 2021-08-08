package me.bscal.betterfarming.mixin.client.block;

import me.bscal.betterfarming.client.BetterFarmingClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LeavesBlock.class) public class LeavesBlockMixin
{

	@Inject(method = "randomDisplayTick", at = @At("TAIL"))
	public void OnRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci)
	{
		if (random.nextInt(16) == 0)
		{
			BlockPos blockPos = pos.down();
			if (CanFallThrough(world.getBlockState(blockPos)))
			{
				double d = (double) pos.getX() + random.nextDouble();
				double e = (double) pos.getY() - 0.05D;
				double f = (double) pos.getZ() + random.nextDouble();
				world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), d, e, f, 0.0D, 0.0D, 0.0D);
			}
		}

		if (random.nextInt(8) == 0)
		{
			double d = (double) pos.getX() + random.nextDouble();
			double e = (double) pos.getY() - 0.05D;
			double f = (double) pos.getZ() + random.nextDouble();
			world.addParticle(new BlockStateParticleEffect(BetterFarmingClient.FALLING_LEAVES, state), d, e, f, 0.0D, 0.0D, 0.0D);
		}
	}

	private static boolean CanFallThrough(BlockState state)
	{
		Material material = state.getMaterial();
		return state.isAir() || state.isIn(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
	}

}
