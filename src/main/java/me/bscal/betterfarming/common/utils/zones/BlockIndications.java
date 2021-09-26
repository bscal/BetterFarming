package me.bscal.betterfarming.common.utils.zones;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class BlockIndications
{
	private static final Random RANDOM = new Random();

	private static DustParticleEffect m_ParticleCache;

	private BlockIndications()
	{
	}

	public static void DrawRect(World world, BlockPos corner1, BlockPos corner2, Vec3f color, float scale)
	{
		if (m_ParticleCache == null || m_ParticleCache.getColor() != color || m_ParticleCache.getScale() != scale)
			m_ParticleCache = new DustParticleEffect(color, scale);

		double y = corner1.getY() + .1;
		Vec3d cornerBL = new Vec3d(corner1.getX(), y, corner1.getZ());
		Vec3d cornerTL = new Vec3d(corner1.getX(), y, corner2.getZ());
		Vec3d cornerTR = new Vec3d(corner2.getX(), y, corner2.getZ());
		Vec3d cornerBR = new Vec3d(corner2.getX(), y, corner1.getZ());

		DrawLine(world, cornerBR, cornerBL, .2D);
		DrawLine(world, cornerTR, cornerBR, .2D);
		DrawLine(world, cornerTL, cornerTR, .2D);
		DrawLine(world, cornerBL, cornerTL, .2D);
	}

	public static void DrawBlockCircle(World world, BlockPos origin, int radius, Vec3f color, float scale)
	{
		if (m_ParticleCache == null || m_ParticleCache.getColor() != color || m_ParticleCache.getScale() != scale)
			m_ParticleCache = new DustParticleEffect(color, scale);

		Set<BlockPos> positions = new HashSet<>();
		BlockPos.iterate(origin.add(-radius, 0, -radius), origin.add(radius, 0, radius)).forEach(pos -> {
			if (pos.isWithinDistance(origin, radius))
				positions.add(pos.toImmutable());
		});
		DrawBlockBorders(world, positions, color, scale);
	}

	/**
	 * Draws a cone of blocks in direction.
	 *
	 * @param length     Length of the cone in blocks
	 * @param startWidth Width of cone at origin
	 * @param step       The increase of the cone per block. (1 = width will increase every 2 blocks, 2 = 1 width per block, 3 = 1 and 2 alternative)
	 */
	public static void DrawBlockCone(World world, BlockPos origin, Direction direction, int length, int startWidth, int step, Vec3f color,
			float scale)
	{
		Set<BlockPos> positions = new HashSet<>();
		BlockPos.Mutable mutablePos = new BlockPos.Mutable();
		Direction leftMostDir = null;
		Direction iterateDir = null;
		if (direction == Direction.NORTH)
		{
			leftMostDir = Direction.WEST;
			iterateDir = Direction.EAST;
		}
		else if (direction == Direction.EAST)
		{
			leftMostDir = Direction.NORTH;
			iterateDir = Direction.SOUTH;
		}
		else if (direction == Direction.SOUTH)
		{
			leftMostDir = Direction.EAST;
			iterateDir = Direction.WEST;
		}
		else if (direction == Direction.WEST)
		{
			leftMostDir = Direction.SOUTH;
			iterateDir = Direction.NORTH;
		}

		for (int i = 0; i < length; i++)
		{
			int width = Math.max(startWidth, i * step + 1);
			if (width % 2 == 0)
				width--; // Checks for even values that wouldnt work with the cone
			mutablePos.set(origin).move(direction, i);
			mutablePos.move(leftMostDir, width / 2);
			for (int j = 0; j < width; j++)
			{
				positions.add(mutablePos.toImmutable());
				mutablePos.move(iterateDir);
			}
		}

		DrawBlockBorders(world, positions, color, scale);
	}

	/**
	 * Draws borders on blocks. If connected to another block will not draw a border there.
	 */
	public static void DrawBlockBorders(World world, Set<BlockPos> blocks, Vec3f color, float scale)
	{
		if (m_ParticleCache == null || m_ParticleCache.getColor() != color || m_ParticleCache.getScale() != scale)
			m_ParticleCache = new DustParticleEffect(color, scale);

		for (BlockPos pos : blocks)
		{
			double y = pos.getY() + .1;
			Vec3d cornerBL = new Vec3d(pos.getX(), y, pos.getZ());
			Vec3d cornerTL = new Vec3d(pos.getX(), y, pos.getZ() + 1D);
			Vec3d cornerTR = new Vec3d(pos.getX() + 1D, y, pos.getZ() + 1D);
			Vec3d cornerBR = new Vec3d(pos.getX() + 1D, y, pos.getZ());

			if (!blocks.contains(pos.north()))
				DrawLine(world, cornerBR, cornerBL, .2D);
			if (!blocks.contains(pos.east()))
				DrawLine(world, cornerTR, cornerBR, .2D);
			if (!blocks.contains(pos.south()))
				DrawLine(world, cornerTL, cornerTR, .2D);
			if (!blocks.contains(pos.west()))
				DrawLine(world, cornerBL, cornerTL, .2D);

		}
	}

	public static void DrawBlockOutline(World world, BlockPos pos, Vec3f color, float scale)
	{
		if (m_ParticleCache == null || m_ParticleCache.getColor() != color || m_ParticleCache.getScale() != scale)
			m_ParticleCache = new DustParticleEffect(color, scale);

		double y = pos.getY() + .1;
		Vec3d cornerBL = new Vec3d(pos.getX(), y, pos.getZ());
		Vec3d cornerTL = new Vec3d(pos.getX(), y, pos.getZ() + 1D);
		Vec3d cornerTR = new Vec3d(pos.getX() + 1D, y, pos.getZ() + 1D);
		Vec3d cornerBR = new Vec3d(pos.getX() + 1D, y, pos.getZ());

		DrawLine(world, cornerBL, cornerTL, .2D);
		DrawLine(world, cornerTL, cornerTR, .2D);
		DrawLine(world, cornerTR, cornerBR, .2D);
		DrawLine(world, cornerBR, cornerBL, .2D);
	}

	public static void FillBlock(World world, BlockPos pos, int count, Vec3f color, float scale)
	{
		if (m_ParticleCache == null || m_ParticleCache.getColor() != color || m_ParticleCache.getScale() != scale)
			m_ParticleCache = new DustParticleEffect(color, scale);

		double y = pos.getY() + .1D;
		Vec3d cornerBL = new Vec3d(pos.getX(), y, pos.getZ());
		Vec3d cornerTR = new Vec3d(pos.getX() + 1D, y, pos.getZ() + 1D);
		while (count > 0)
		{
			count--;
			double d = (RANDOM.nextDouble() * (cornerTR.getX() - cornerBL.getX()) + cornerBL.getX());
			double e = (RANDOM.nextDouble() * (cornerTR.getY() - cornerBL.getY()) + cornerBL.getY());
			double f = (RANDOM.nextDouble() * (cornerTR.getZ() - cornerBL.getZ()) + cornerBL.getZ());
			world.addParticle(m_ParticleCache, d, e, f, 0.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * @param step 0.0 - 1.0 - lower values will increase the number of particles
	 */
	public static void DrawLine(World world, Vec3d from, Vec3d to, double step)
	{
		double delta = 0.0;
		while (delta < 1f)
		{
			Vec3d pos = from.lerp(to, delta);
			delta += step;
			world.addParticle(m_ParticleCache, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Similar to DrawLine but draws lines up to height
	 *
	 * @param step 0.0 - 1.0 - lower values will increase the number of particles
	 */
	public static void DrawWall(World world, Vec3d from, Vec3d to, double height, double step, Vec3f color, float scale)
	{
		if (m_ParticleCache == null || m_ParticleCache.getColor() != color || m_ParticleCache.getScale() != scale)
			m_ParticleCache = new DustParticleEffect(color, scale);

		double increaseInHeight = height * step;
		double heightDelta = 0.0;
		while (heightDelta < height)
		{
			heightDelta += increaseInHeight;
			double delta = 0.0;
			while (delta < 1f)
			{
				Vec3d pos = from.lerp(to, delta);
				delta += step;
				world.addParticle(m_ParticleCache, pos.getX(), pos.getY() + heightDelta, pos.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	public static void DrawCircle(World world, Vec3d origin, double radius, double density, Vec3f color, float scale)
	{
		if (m_ParticleCache == null || m_ParticleCache.getColor() != color || m_ParticleCache.getScale() != scale)
			m_ParticleCache = new DustParticleEffect(color, scale);

		double y = origin.getY() + .1D;
		for (int i = 0; i < density * 2; i++)
		{
			double radian = (Math.PI / density) * i;
			double x = origin.getX() + radius * Math.cos(radian);
			double z = origin.getZ() + radius * Math.sin(radian);
			world.addParticle(m_ParticleCache, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	public static void DrawPoints(World world, Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, double step, Vec3f color, float scale)
	{
		if (m_ParticleCache == null || m_ParticleCache.getColor() != color || m_ParticleCache.getScale() != scale)
			m_ParticleCache = new DustParticleEffect(color, scale);

		DrawLine(world, p0, p1, step);
		DrawLine(world, p1, p2, step);
		DrawLine(world, p2, p3, step);
		DrawLine(world, p3, p0, step);
	}
}
