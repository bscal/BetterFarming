package me.bscal.betterfarming.common.database.blockdata.worldpos;

import me.bscal.betterfarming.common.database.blockdata.DataManager;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataChunk;
import me.bscal.betterfarming.common.database.blockdata.IBlockDataWorld;
import me.bscal.betterfarming.common.database.blockdata.smart.SmartDataChunk;
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

public class WorldPosWorld implements IBlockDataWorld
{

	protected final ServerWorld world;
	protected final WorldPosDataManager parent;
	protected final WorldPosChunk m_dataChunk;
	protected final File m_saveDir;

	public WorldPosWorld( ServerWorld world, WorldPosDataManager parent)
	{
		this.world = world;
		this.parent = parent;
		this.m_dataChunk = new WorldPosChunk(this);
		this.m_saveDir = new File(DimensionType.getSaveDirectory(world.getRegistryKey(), world.getServer()
				.getSavePath(WorldSavePath.ROOT)
				.toFile()) + "/data/" + parent.id + "/");
		this.m_saveDir.mkdirs();
	}

	@Override
	public ServerWorld GetWorld()
	{
		return world;
	}

	@Override
	public IBlockDataChunk GetOrCreateChunk(ChunkPos pos)
	{
		return m_dataChunk;
	}

	public WorldPosChunk Get()
	{
		return m_dataChunk;
	}

	@Override
	public IBlockDataChunk Get(ChunkPos pos)
	{
		return m_dataChunk;
	}

	@Override
	public void Remove(BlockPos pos)
	{
		m_dataChunk.RemoveBlock(pos);
	}

	@Override
	public void OnLoadChunk(ServerWorld world, WorldChunk chunk)
	{
		ChunkPos pos = chunk.getPos();
		File file = new File(m_saveDir, DataManager.ChunkFileName(pos.x, pos.z));
		if (file.exists())
		{
			try
			{
				NbtCompound nbt = NbtIo.readCompressed(file);
				m_dataChunk.FromNbt(nbt);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void OnUnloadChunk(ServerWorld world, WorldChunk chunk)
	{
		ChunkPos pos = chunk.getPos();
		File file = new File(m_saveDir, DataManager.ChunkFileName(pos.x, pos.z));
		NbtCompound root = new NbtCompound();
		try
		{
			var data = m_dataChunk.ChunkToNbt(pos, root);
			if (data != null)
				NbtIo.writeCompressed(data, file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void Save()
	{
		for (var pair : m_dataChunk.m_blockData.long2ObjectEntrySet())
		{
			ChunkPos pos = new ChunkPos(pair.getLongKey());
			File file = new File(m_saveDir, DataManager.ChunkFileName(pos.x, pos.z));
			NbtCompound root = new NbtCompound();
			try
			{
				var data = m_dataChunk.ChunkToNbt(pos, root);
				if (data != null)
					NbtIo.writeCompressed(data, file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
