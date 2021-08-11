package me.bscal.betterfarming.common.database.blockdata;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.bscal.betterfarming.BetterFarming;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PersistentBlockDataManagerV2
{

	public static class PersistentBlockDataChunk
	{
		private Int2ObjectOpenHashMap<PersistentBlockData[][]> m_blockData;

		public PersistentBlockDataChunk()
		{
			m_blockData = new Int2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
		}

		public NbtCompound ToNbt(NbtCompound nbt)
		{
			NbtList yList = new NbtList();
			for (var pair : m_blockData.int2ObjectEntrySet())
			{
				NbtCompound compound = new NbtCompound();
				compound.putInt("y", pair.getIntKey());
				NbtList xzList = new NbtList();
				for (int i = 0; i < pair.getValue().length; i++)
				{
					for (int j = 0; j < pair.getValue()[i].length; j++)
					{
						xzList.add(pair.getValue()[i][j].ToNbt(new NbtCompound()));
					}
				}
				compound.put("xz-data", xzList);
				yList.add(compound);
			}
			nbt.put("y-data", yList);
			return nbt;
		}

		public void FromNbt(NbtCompound nbt)
		{
			NbtList yList = nbt.getList("y-data", NbtElement.COMPOUND_TYPE);
			for (NbtElement eleZ : yList)
			{
				if (eleZ instanceof NbtCompound compound)
				{
					PersistentBlockDataChunk dataChunk = new PersistentBlockDataChunk();
					int y = compound.getInt("y");
					for (NbtElement eleXY : compound.getList("xz-data", NbtElement.COMPOUND_TYPE))
					{
						int x = compound.getInt("x");
						int z = compound.getInt("z");
						dataChunk.CreateAndPut(x, y, z, new PersistentBlockData((NbtCompound) eleXY));
					}
				}
			}
		}

		public PersistentBlockData Get(int x, int y, int z)
		{
			return m_blockData.get(y)[x][z];
		}

		public PersistentBlockData GetOrCreate(int x, int y, int z)
		{
			var data = m_blockData.get(y)[x][z];
			if (data == null)
			{
				Create(y);
				return m_blockData.get(y)[x][z];
			}
			return data;
		}

		public void Put(int x, int y, int z, PersistentBlockData data)
		{
			m_blockData.get(y)[x][z] = data;
		}

		public void Create(int y)
		{
			PersistentBlockData[][] data2DArray = new PersistentBlockData[16][16];
			Arrays.fill(data2DArray, new PersistentBlockData());
			m_blockData.put(y, data2DArray);
		}

		public void CreateAndPut(int x, int y, int z, PersistentBlockData data)
		{
			if (!m_blockData.containsKey(y))
				Create(y);
			Put(x, y, z, data);
		}

	}

	public static class PersistentBlockDataWorld
	{
		private ServerWorld m_world;
		private File m_saveDir;
		private Long2ObjectOpenHashMap<PersistentBlockDataChunk> m_chunkToSection;

		public PersistentBlockDataWorld(ServerWorld world)
		{
			this.m_world = world;
			this.m_saveDir = new File(world.getServer()
					.getSavePath(WorldSavePath.ROOT) + "/data/" + BetterFarming.MOD_ID + "_chunk_block_data/" + world.getRegistryKey()
					.getValue() + File.separatorChar);
			this.m_saveDir.mkdirs();
			this.m_chunkToSection = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
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
			m_chunkToSection.clear();
		}

		public void OnLoad(ServerWorld world, WorldChunk chunk)
		{
			ChunkPos pos = chunk.getPos();
			File file = new File(m_saveDir, ChunkFileName(pos.x, pos.z));
			if (file.exists())
			{
				try
				{
					NbtCompound nbt = NbtIo.readCompressed(file);
					var dataChunk = new PersistentBlockDataChunk();
					dataChunk.FromNbt(nbt);
					m_chunkToSection.put(pos.toLong(), dataChunk);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		public void OnUnload(ServerWorld world, WorldChunk chunk)
		{
			ChunkPos pos = chunk.getPos();
			File file = new File(m_saveDir, ChunkFileName(pos.x, pos.z));
			var dataChunk = m_chunkToSection.remove(pos.toLong());

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

	public final ObjectArrayList<PersistentBlockDataWorld> worlds;

	public PersistentBlockDataManagerV2(MinecraftServer server)
	{
		var worldSet = server.getWorldRegistryKeys();
		this.worlds = new ObjectArrayList<>(worldSet.size());
	}

	public PersistentBlockDataWorld SetupWorld(ServerWorld world)
	{
		var dataWorld = new PersistentBlockDataWorld(world);
		worlds.add(dataWorld);
		return dataWorld;
	}

	public void OnLoad(ServerWorld world, WorldChunk chunk)
	{
		for (var worldData : worlds)
		{
			if (worldData.m_world.equals(world))
			{
				worldData.OnLoad(world, chunk);
				break;
			}
		}
	}

	public void OnUnload(ServerWorld world, WorldChunk chunk)
	{
		for (var worldData : worlds)
		{
			if (worldData.m_world.equals(world))
			{
				worldData.OnUnload(world, chunk);
				break;
			}
		}
	}

	public void Save()
	{
		worlds.forEach(PersistentBlockDataWorld::Save);
	}

	private static String ChunkFileName(int x, int z)
	{
		return x + "-" + z + ".dat";
	}
}
