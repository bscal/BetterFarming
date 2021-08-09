package me.bscal.betterfarming.mixin.client.block;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.common.seasons.SeasonSettings;
import me.bscal.betterfarming.common.seasons.Seasons;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.BlockStateParticleEffect;
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

	@Inject(method = "randomDisplayTick", at = @At(value = "HEAD"))
	public void OnRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci)
	{
		int leafDist = SeasonSettings.Root.leafFallingDistance.getValue();
		if (leafDist > 0 && pos.isWithinDistance(MinecraftClient.getInstance().player.getPos(), leafDist))
		{
			int bound = Seasons.GetSeason() == Seasons.AUTUMN ? 32 : 80;

			if (world.isRaining())
				bound -= 8;
			if (world.isThundering())
				bound -= 8;

			if (random.nextInt(bound) == 0)
			{
				BlockPos blockPos = pos.down();
				if (CanFallThrough(world.getBlockState(blockPos)))
				{
					double d = (double) pos.getX() + random.nextDouble();
					double e = (double) pos.getY() - 0.05D;
					double f = (double) pos.getZ() + random.nextDouble();
					world.addParticle(new BlockStateParticleEffect(BetterFarmingClient.FALLING_LEAVES, state), d, e, f, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

	private static boolean CanFallThrough(BlockState state)
	{
		return state.isAir() || state.isIn(BlockTags.FIRE) || state.getMaterial().isReplaceable();
	}

}
