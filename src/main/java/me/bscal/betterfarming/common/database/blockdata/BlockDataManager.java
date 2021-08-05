package me.bscal.betterfarming.common.database.blockdata;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.seasons.SeasonalCrop;
import me.bscal.betterfarming.common.seasons.Seasons;
import me.bscal.betterfarming.common.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.function.Supplier;

public class BlockDataManager extends PersistentState
{
	private ServerWorld world;

	// Serialized
	private final Object2ObjectOpenHashMap<BlockPos, BlockData> m_blockDataMap;

	//private Object2ObjectOpenHashMap<WorldPos, BlockData> m_blockDataMap;
	//private final ObjectArrayList<World> m_worldCache;
	//for future use: cache gson for saving throughout lifetime

	protected BlockDataManager()
	{
		m_blockDataMap = new Object2ObjectOpenHashMap<>();
	}

	protected BlockDataManager(int expected)
	{
		m_blockDataMap = new Object2ObjectOpenHashMap<>(expected + 16);
	}

	public static BlockDataManager GetOrCreate(ServerWorld world)
	{
		BlockDataManager manager = world.getPersistentStateManager()
				.getOrCreate(BlockDataManager::readNbt, BlockDataManager::new, BetterFarming.MOD_ID + "_blockdata");
		manager.world = world;
		return manager;
	}

	private static BlockDataManager readNbt(NbtCompound nbt)
	{
		BlockDataManager dataManager;

		if (nbt.get("") instanceof NbtList list)
		{
			int size = list.size();
			dataManager = new BlockDataManager(size);

			for (int i = 0; i < size; i++)
			{
				NbtCompound entry = list.getCompound(i);
				BlockPos pos = new BlockPos(Utils.Vec3iFromShortString(entry.getString("key")));
				BlockData data = BlockData.Serializer.deserializeNbt(entry);
				dataManager.m_blockDataMap.put(pos, data);
			}
		}
		else
			dataManager = new BlockDataManager();
		return dataManager;
	}

	@Override
	public boolean isDirty()
	{
		return true;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt)
	{
		NbtList list = new NbtList();
		m_blockDataMap.forEach(((blockPos, blockData) -> {
			NbtCompound nextEntry = new NbtCompound();
			nextEntry.putString("key", blockPos.toShortString());
			BlockData.Serializer.serializeNbt(blockData, nextEntry);
			list.add(nextEntry);
		}));
		nbt.put("", list);
		return nbt;
	}

	public void UpdateUnloadedEntries(MinecraftServer server, float estimatedRandomTicksPassed)
	{
		var entrySet = m_blockDataMap.object2ObjectEntrySet();
		for (var pair : entrySet)
		{
			boolean chunkLoaded = world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pair.getKey().getX()),
					ChunkSectionPos.getSectionCoord(pair.getKey().getZ()));
			// Estimated time that if BlockData has not been updated since, we can presume that the chunk is unloaded and does not update.
			if (!chunkLoaded)
			{
				// We can presume that when the block was unloaded and the last tick was a success that it can continue to tick while
				// unloaded. It is incremented with estimated # of randomTicks and should not cause unloaded chunks to load.
				UpdateGrowable(pair.getKey(), pair.getValue(), world, estimatedRandomTicksPassed);
			}
		}
	}

	private void UpdateGrowable(BlockPos pos, BlockData data, World world, float estimatedRandomTicksPassed)
	{
		if (data.ableToGrow)
		{
			SeasonalCrop crop = BetterFarming.CROP_MANAGER.seasonalCrops.get(data.block);
			Biome biome = world.getBiome(pos);
			int season = Seasons.GetSeasonForBiome(biome, BetterFarming.SEASON_CLOCK.currentSeason);
			data.lastUpdate = BetterFarming.SEASON_CLOCK.ticksSinceCreation;

			boolean isRandomTickLessThan1 = estimatedRandomTicksPassed < 1f && BetterFarming.RAND.nextFloat() < estimatedRandomTicksPassed;
			data.growthTime -= crop.growthRate[season] * ((isRandomTickLessThan1) ?
					(int) estimatedRandomTicksPassed :
					Math.max((int) estimatedRandomTicksPassed, 1));
		}
		BetterFarming.LOGGER.info("Updated unloaded BlockData " + data.lastUpdate);

	}

	public BlockData GetOrCreateEntry(BlockPos pos, Supplier<BlockData> factory)
	{
		return m_blockDataMap.getOrDefault(pos, Create(pos, factory));
	}

	public BlockData GetOrCreateEntry(BlockPos pos, int growthTime, Block block)
	{
		BlockData data = m_blockDataMap.get(pos);
		return data != null ?
				data :
				Create(pos, () -> new BlockData(BetterFarming.GetTime(), growthTime, 0, block));
	}

	public BlockData Create(BlockPos pos, Supplier<BlockData> factory)
	{
		BlockData data = factory.get();
		m_blockDataMap.put(pos.toImmutable(), data);
		return data;
	}

	public Object2ObjectOpenHashMap<BlockPos, BlockData> GetDataMap()
	{
		return m_blockDataMap;
	}

	/*public BlockData GetOrCreate(WorldPos worldPos, Supplier<BlockData> factory)
	{
		return m_blockDataMap.getOrDefault(worldPos, Create(worldPos, factory));
	}

	public BlockData GetOrCreate(WorldPos worldPos, int growthTime, Block block)
	{
		return m_blockDataMap.getOrDefault(worldPos, Create(worldPos,
				() -> new BlockData(SeasonManager.GetOrCreate().GetSeasonClock().ticksSinceCreation, growthTime, 0, block)));
	}

	public BlockData Create(WorldPos pos, Supplier<BlockData> factory)
	{
		BlockData data = factory.get();
		m_blockDataMap.put(pos, data);
		return data;
	}

	public void Save(String path)
	{
		Utils.WriteJsonToFile(path, GetGsonInstance().toJson(m_blockDataMap));
	}

	public void Load(String path)
	{
		Type type = new TypeToken<Object2ObjectOpenHashMap<WorldPos, BlockData>>()
		{
		}.getType();
		m_blockDataMap = Utils.ReadJsonFromFile(path, GetGsonInstance(), type);
		if (m_blockDataMap == null)
			m_blockDataMap = new Object2ObjectOpenHashMap<>();
		m_blockDataMap.defaultReturnValue(new BlockData(-1, -1, -1, Blocks.AIR));
	}

	private Gson GetGsonInstance()
	{
		return new GsonBuilder().enableComplexMapKeySerialization()
				.registerTypeAdapter(WorldPos.class, new WorldPos.Serializer())
				.registerTypeAdapter(BlockData.class, new BlockData.Serializer())
				.create();
	}*/
}
