package me.bscal.betterfarming.common.utils.zones;

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockIndications
{
	private static final Random RANDOM = new Random();

	private static DustParticleEffect m_ParticleCache;

	public static void DrawRect(World world, BlockPos corner1, BlockPos corner2, Vec3f color, float scale)
	{
		DrawBorders(world, BlockPos.stream(corner1, corner2).collect(Collectors.toSet()), color, scale);
	}

	public static void DrawCircle(World world, BlockPos origin, int radius, Vec3f color, float scale)
	{
		DrawBorders(world, BlockPos.streamOutwards(origin, radius, 0, radius).collect(Collectors.toSet()), color, scale);
	}

	/**]
	 *
	 * @param world
	 * @param origin
	 * @param direction
	 * @param length
	 * @param startWidth
	 * @param step
	 * @param color
	 * @param scale
	 */
	public static void DrawCone(World world, BlockPos origin, Direction direction, int length, int startWidth, int step, Vec3f color, float scale)
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
			if (width % 2 == 0) width--;
			mutablePos.set(origin).move(direction, i);
			mutablePos.move(leftMostDir, width / 2);
			for (int j = 0; j < width; j++)
			{
				positions.add(mutablePos.toImmutable());
				mutablePos.move(iterateDir);
			}
		}

		DrawBorders(world, positions, color, scale);
	}

	public static void DrawBorders(World world, Set<BlockPos> blocks, Vec3f color, float scale)
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

}
