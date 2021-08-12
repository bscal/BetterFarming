package me.bscal.betterfarming.common.database.blockdata;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.bscal.betterfarming.BetterFarming;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.IOException;

public abstract class DataWorld implements IBlockDataWorld
{

	protected ServerWorld m_world;
	protected File m_saveDir;
	protected Long2ObjectOpenHashMap<IBlockDataChunk> m_chunkToSection;

	public DataWorld(ServerWorld world)
	{
		this.m_world = world;
		this.m_saveDir = new File(DimensionType.getSaveDirectory(world.getRegistryKey(),
				world.getServer().getSavePath(WorldSavePath.ROOT).toFile()) + "/data/" + BetterFarming.MOD_ID + "_block_data/");
		this.m_saveDir.mkdirs();
		this.m_chunkToSection = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
	}

	@Override
	public IBlockDataChunk Get(ChunkPos pos)
	{
		return m_chunkToSection.get(pos.toLong());
	}

	public ServerWorld GetWorld()
	{
		return m_world;
	}

	public void Save()
	{
		for (var pair : m_chunkToSection.long2ObjectEntrySet())
		{
			ChunkPos pos = new ChunkPos(pair.getLongKey());
			File file = new File(m_saveDir, ChunkFileName(pos.x, pos.z));
			NbtCompound root = new NbtCompound();
			try
			{
				NbtIo.writeCompressed(pair.getValue().ToNbt(root), file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void OnUnloadChunk(ServerWorld world, WorldChunk chunk)
	{
		ChunkPos pos = chunk.getPos();
		File file = new File(m_saveDir, ChunkFileName(pos.x, pos.z));
		IBlockDataChunk dataChunk = m_chunkToSection.remove(pos.toLong());

		if (dataChunk == null)
			return;

		NbtCompound root = new NbtCompound();
		try
		{
			NbtIo.writeCompressed(dataChunk.ToNbt(root), file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static String ChunkFileName(int x, int z)
	{
		return x + "-" + z + ".dat";
	}

}
