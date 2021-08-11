package me.bscal.betterfarming.common.database.blockdata;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import me.bscal.betterfarming.BetterFarming;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class BlockDataChunkManager
{

	private Long2ObjectOpenHashMap<BlockDataChunk> m_chunkToSection;
	private ServerWorld m_world;
	private File m_file;

	public BlockDataChunkManager(ServerWorld world)
	{
		this.m_world = world;
		this.m_file = new File(world.getServer()
				.getSavePath(WorldSavePath.ROOT) + "/data/" + BetterFarming.MOD_ID + "_chunk_block_data/" + world.getRegistryKey()
				.getValue() + File.separatorChar);
		this.m_file.mkdirs();
		this.m_chunkToSection = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
	}

	public BlockDataChunk SetDataChunk(ChunkPos pos)
	{
		var d = new BlockDataChunk();
		m_chunkToSection.put(pos.toLong(), d);
		return d;
	}

	public BlockDataChunk GetOrCreateDataChunk(ChunkPos pos)
	{
		boolean chunkLoaded = m_world.isChunkLoaded(pos.x, pos.z);

		if (chunkLoaded)
		{
			var dataChunk = m_chunkToSection.get(pos.toLong());
			return dataChunk == null ? SetDataChunk(pos) : dataChunk;
		}
		else
			return BlockDataChunk.UNLOADED;
	}

	public void OnLoad(ServerWorld world, WorldChunk chunk)
	{
		ChunkPos pos = chunk.getPos();
		File file = new File(m_file, ChunkFileName(pos.x, pos.z));
		if (file.exists())
		{
			try
			{
				NbtCompound nbt = NbtIo.readCompressed(file);
				m_chunkToSection.put(pos.toLong(), new BlockDataChunk(nbt));
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
		File file = new File(m_file, ChunkFileName(pos.x, pos.z));
		BlockDataChunk blockDataChunk = m_chunkToSection.remove(pos.toLong());

		if (blockDataChunk == null)
			return;

		NbtCompound root = new NbtCompound();
		try
		{
			blockDataChunk.isLoaded = false;
			NbtIo.writeCompressed(blockDataChunk.ToNbt(root), file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void Save(ServerWorld world)
	{
		for (var pair : m_chunkToSection.long2ObjectEntrySet())
		{
			ChunkPos pos = new ChunkPos(pair.getLongKey());
			File file = new File(m_file, ChunkFileName(pos.x, pos.z));
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

	private String ChunkFileName(int x, int z)
	{
		return x + "-" + z + ".dat";
	}

	public static class BlockDataChunk
	{
		public static final BlockDataChunk UNLOADED = new BlockDataChunk(0, false);

		private Int2ObjectOpenHashMap<BlockDataChunkSection> m_yToXZMap;
		public boolean isLoaded;

		public BlockDataChunk()
		{
			this(Hash.DEFAULT_INITIAL_SIZE, true);
		}

		public BlockDataChunk(int expected, boolean isLoaded)
		{
			this.m_yToXZMap = new Int2ObjectOpenHashMap<>(expected);
			this.isLoaded = isLoaded;
		}

		public BlockDataChunk(NbtCompound nbt)
		{
			NbtList list = nbt.getList("data", NbtElement.COMPOUND_TYPE);
			m_yToXZMap = new Int2ObjectOpenHashMap<>(list.size() + 8, 1f);
			list.forEach((element -> {
				if (element instanceof NbtCompound compound)
				{
					m_yToXZMap.put(compound.getInt("y"), new BlockDataChunkSection(compound));
				}
			}));
			this.isLoaded = true;
		}

		public NbtCompound ToNbt(NbtCompound nbt)
		{
			NbtList list = new NbtList();
			for (var pair : m_yToXZMap.int2ObjectEntrySet())
			{
				NbtCompound entry = new NbtCompound();
				entry.putInt("y", pair.getIntKey());
				pair.getValue().ToNbt(entry);
				list.add(entry);
			}
			nbt.put("sections", list);
			return nbt;
		}

		public BlockDataChunkSection GetSection(int y)
		{
			var x = m_yToXZMap.get(y);
			if (x == null)
			{
				var d = new BlockDataChunkSection();
				m_yToXZMap.put(y, d);
				return d;
			}
			return x;
		}
	}

	public static class BlockDataChunkSection
	{
		private final Long2ObjectOpenHashMap<PersistentBlockData> m_blockDataMap;

		public BlockDataChunkSection()
		{
			m_blockDataMap = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
		}

		public BlockDataChunkSection(NbtCompound nbt)
		{
			m_blockDataMap = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1f);
			NbtList list = nbt.getList("data", NbtElement.COMPOUND_TYPE);
			list.forEach(compound -> {
				SetBlockData(nbt.getLong("xz"), new PersistentBlockData(nbt));
			});
		}

		public NbtCompound ToNbt(NbtCompound nbt)
		{
			NbtList list = new NbtList();
			for (var pair : m_blockDataMap.long2ObjectEntrySet())
			{
				NbtCompound compound = new NbtCompound();
				compound.putLong("xz", pair.getLongKey());
				pair.getValue().ToNbt(compound);
				list.add(compound);
			}
			nbt.put("data", list);
			return nbt;
		}

		public long XZToLong(int x, int z)
		{
			return x & 0xffL << 32 | z & 0xffL;
		}

		public PersistentBlockData GetBlockData(int x, int z)
		{
			return m_blockDataMap.get(XZToLong(x, z));
		}

		public PersistentBlockData SetBlockData(long xz, PersistentBlockData data)
		{
			return m_blockDataMap.put(xz, data);
		}

		public PersistentBlockData SetBlockData(int x, int z, PersistentBlockData data)
		{
			return m_blockDataMap.put(XZToLong(x, z), data);
		}

		public Long2ObjectOpenHashMap<PersistentBlockData> GetMap()
		{
			return m_blockDataMap;
		}

		public Long2ObjectRBTreeMap<PersistentBlockData> GetSortedMap()
		{
			return new Long2ObjectRBTreeMap<>(m_blockDataMap);
		}

		public List<PersistentBlockData> GetRandomList()
		{
			List<PersistentBlockData> list = m_blockDataMap.values().stream().toList();
			Collections.shuffle(list);
			return list;
		}
	}

}
