package me.bscal.betterfarming.common.database.blockdata;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.utils.LongPair;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class DataWorld implements IBlockDataWorld
{

	protected ServerWorld m_world;
	protected File m_saveDir;
	protected Long2ObjectOpenHashMap<IBlockDataChunk> m_chunkToSection;

	public DataWorld(String id, ServerWorld world)
	{
		this.m_world = world;
		Path savePath = DimensionType.getSaveDirectory(world.getRegistryKey(), world.getServer().getSavePath(WorldSavePath.ROOT));
		this.m_saveDir = new File(savePath.toFile(), Path.of("/data/", id, "/").toString());
		this.m_saveDir.mkdirs();
		this.m_chunkToSection = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
	}

	@Override
	public IBlockDataChunk Get(ChunkPos pos)
	{
		return m_chunkToSection.get(pos.toLong());
	}

	public ServerWorld GetServerWorld()
	{
		return m_world;
	}

	public void Remove(BlockPos pos)
	{
		ChunkPos chunkPos = m_world.getChunk(pos).getPos();
		var chunk = Get(chunkPos);
		if (chunk != null)
		{
			chunk.RemoveBlock(pos);
			if (chunk.Size() < 1)
				m_chunkToSection.remove(chunkPos.toLong());
		}
	}

	@Override
	public IBlockDataBlock[] GetAll(ServerWorld world)
	{
		Stream<IBlockDataBlock> stream = Stream.of();
		for (var chunk : m_chunkToSection.values())
			stream = Stream.concat(stream, Arrays.stream(chunk.GetAll(world)));
		return (IBlockDataBlock[]) stream.toArray();
	}

	@Override
	public IBlockDataBlock[] GetAllChunk(ServerWorld world, ChunkPos pos)
	{
		var chunk = Get(pos);
		if (chunk == null)
			return new IBlockDataBlock[0];
		return chunk.GetAll(world);
	}

	@Override
	public void ForEach(Consumer<IBlockDataBlock> foreach)
	{
		m_chunkToSection.values().forEach(value -> value.ForEach(foreach));
	}

	public void Save()
	{
		for (var pair : m_chunkToSection.long2ObjectEntrySet())
		{
			ChunkPos pos = new ChunkPos(pair.getLongKey());
			File file = new File(m_saveDir, DataManager.ChunkFileName(pos.x, pos.z));
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

	public void Load()
	{
		String[] content = m_saveDir.list();
		for (String fileName : content)
		{
			String[] extensionSplit = fileName.split("/.");
			if (extensionSplit.length != 2 || extensionSplit[1].equals("dat"))
				continue;

			String[] nameSplit = fileName.split("_");
			if (nameSplit.length != 2)
				continue;

			int chunkX = Integer.parseInt(extensionSplit[0]);
			int chunkZ = Integer.parseInt(extensionSplit[1]);
			ChunkPos pos = new ChunkPos(chunkX, chunkZ);
			OnUnloadChunk(m_world, pos);
		}

		for (var pair : m_chunkToSection.long2ObjectEntrySet())
		{
			ChunkPos pos = new ChunkPos(pair.getLongKey());
			File file = new File(m_saveDir, DataManager.ChunkFileName(pos.x, pos.z));
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

	public void OnUnloadChunk(ServerWorld world, ChunkPos pos)
	{
		File file = new File(m_saveDir, DataManager.ChunkFileName(pos.x, pos.z));
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

}
