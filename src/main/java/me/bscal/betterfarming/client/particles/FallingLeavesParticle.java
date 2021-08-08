package me.bscal.betterfarming.client.particles;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.common.seasons.Seasons;
import me.bscal.betterfarming.common.utils.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class FallingLeavesParticle extends SpriteBillboardParticle
{

	// TODO Cleanup, wind?, rain speeds? seasonal effects? This doesnt need a complex factory prob

	private final float angleVelocity;

	protected FallingLeavesParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i,
			SpriteProvider sprites, BlockState state)
	{
		super(clientWorld, d, e, f, g, h, i);
		this.setSprite(sprites.getSprite(clientWorld.random));
		this.scale *= clientWorld.random.nextFloat() * .25f + .25f;
		int max = 48;
		this.maxAge = max - clientWorld.random.nextInt(16);

		var b = clientWorld.getBiome(new BlockPos(d, e, f));
		clientWorld.getRegistryManager().get(Registry.BIOME_KEY).getKey(b).ifPresent((key) -> {
			var changer = BetterFarmingClient.GetBiomeSeasonHandler().biomeEffectChangerMap.get(key);
			Color c = new Color(changer.GetFoliageColorWithFall(Seasons.GetSeasonForBiome(b, Seasons.GetSeason()), (int) d, (int) f));
			this.setColor(c.r / 255f, c.g / 255f, c.b / 255f);
		});
		this.velocityX = clientWorld.random.nextFloat() * .1f - .05f;
		this.velocityY = 0;
		this.velocityZ = clientWorld.random.nextFloat() * .1f - .05f;
		this.angleVelocity = clientWorld.random.nextFloat() - 0.5f;
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	public void tick()
	{
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge)
		{
			this.markDead();
		}
		else
		{
			this.prevAngle = this.angle;
			this.angle += this.angleVelocity;

			if (this.onGround)
			{
				this.prevAngle = this.angle = 0.0F;
			}
			else
			{
				this.move(this.velocityX, this.velocityY, this.velocityZ);
				this.velocityY -= 0.003000000026077032D;
				this.velocityY = Math.max(this.velocityY, -0.14000000059604645D);
			}
		}
	}

	@Environment(EnvType.CLIENT) public static class Factory implements ParticleFactory<BlockStateParticleEffect>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(BlockStateParticleEffect parameters, ClientWorld world, double x, double y, double z,
				double velocityX, double velocityY, double velocityZ)
		{
			BlockState blockState = parameters.getBlockState();
			return new FallingLeavesParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider, blockState);
		}
	}
}
