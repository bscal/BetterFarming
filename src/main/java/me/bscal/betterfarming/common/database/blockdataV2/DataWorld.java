package me.bscal.betterfarming.common.database.blockdataV2;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.function.Supplier;

public class DataWorld
{

	public final ServerWorld world;
	private final File m_SaveFile;
	private final Long2ObjectOpenHashMap<IDataBlock> m_BlockData;
	private final Supplier<IDataBlock> m_DataFactory;

	public DataWorld(ServerWorld world, Supplier<IDataBlock> dataFactory)
	{
		this.world = world;
		Path savePath = DimensionType.getSaveDirectory(world.getRegistryKey(), world.getServer().getSavePath(WorldSavePath.ROOT));
		this.m_SaveFile = new File(savePath.toFile(), "blockdata");
		this.m_SaveFile.mkdirs();
		this.m_BlockData = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, 1.0f);
		this.m_DataFactory = dataFactory;
	}

	public IDataBlock GetOrCreate(BlockPos pos)
	{
		return GetOrCreate(pos, m_DataFactory);
	}

	public IDataBlock GetOrCreate(BlockPos pos, Supplier<IDataBlock> dataFactory)
	{
		long posLong = pos.asLong();
		IDataBlock dataBlock = m_BlockData.get(posLong);
		if (dataBlock == null && m_DataFactory != null)
		{
			dataBlock = dataFactory.get();
			m_BlockData.put(posLong, dataBlock);
			return dataBlock;
		}
		return dataBlock;
	}

	public boolean Contains(BlockPos pos)
	{
		return m_BlockData.containsKey(pos.asLong());
	}

	public Long2ObjectOpenHashMap<IDataBlock> GetMap()
	{
		return m_BlockData;
	}

	public void Serialize()
	{
		NbtCompound root = new NbtCompound();
		NbtList list = new NbtList();
		for (var pair : m_BlockData.long2ObjectEntrySet())
		{
			NbtCompound entry = new NbtCompound();
			entry.putLong("key", pair.getLongKey());
			entry.putString("class", pair.getValue().getClass().getName());
			entry.put("value", pair.getValue().AsNbt());
			list.add(entry);
		}
		root.put("list", list);

		try
		{
			NbtIo.writeCompressed(root, new File(m_SaveFile, "data.dat"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void Deserialize()
	{
		File saveFile = new File(m_SaveFile, "data.dat");
		if (saveFile.exists())
		{
			try
			{
				NbtCompound root = NbtIo.readCompressed(saveFile);
				if (root.get("list") instanceof NbtList list)
				{
					for (NbtElement ele : list)
					{
						if (ele instanceof NbtCompound entry)
						{
							Create(entry);
						}
					}
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void Create(NbtCompound entry)
	{
		long key = entry.getLong("key");
		String clazzName = entry.getString("class");
		try
		{
			IDataBlock newInstanceOf = (IDataBlock) Class.forName(clazzName).getConstructor().newInstance();
			newInstanceOf.FromNbt(entry);
			m_BlockData.put(key, newInstanceOf);
		}
		catch (NoSuchMethodException e)
		{
			System.err.println("[ Error ] Your class " + clazzName + " does not contain a default constructor!");
			e.printStackTrace();
		}
		catch (InstantiationException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

}
