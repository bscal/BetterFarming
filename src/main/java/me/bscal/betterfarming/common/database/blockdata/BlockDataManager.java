package me.bscal.betterfarming.common.database.blockdata;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.seasons.SeasonManager;
import me.bscal.betterfarming.common.seasons.SeasonalCrop;
import me.bscal.betterfarming.common.seasons.Seasons;
import me.bscal.betterfarming.common.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Optional;
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

	public Optional<BlockData> Get(WorldPos worldPos)
	{
		BlockData data = m_blockDataMap.get(worldPos);
		return (data == null) ? Optional.empty() : Optional.of(data);
	}

	public void UpdateUnloadedEntries(MinecraftServer server, int estimatedRandomTicksPassed)
	{
		var entrySet = m_blockDataMap.object2ObjectEntrySet();
		for (var pair : entrySet)
		{
			boolean chunkLoaded = world.getChunkManager()
					.isChunkLoaded(ChunkSectionPos.getSectionCoord(pair.getKey().getX()),
							ChunkSectionPos.getSectionCoord(pair.getKey().getZ()));
			//TODO
			if (chunkLoaded)
			{
				BlockState state = world.getBlockState(pair.getKey());
				if (state.isAir() || !state.isOf(pair.getValue().block) || UpdateGrowable(pair.getKey(), pair.getValue(), state, world,
						estimatedRandomTicksPassed))
					entrySet.remove(pair); // Removed
			}
		}
	}

	private boolean UpdateGrowable(BlockPos pos, BlockData data, BlockState state, World world, int estimatedRandomTicksPassed)
	{
		SeasonalCrop crop = BetterFarming.CROP_MANAGER.seasonalCrops.get(data.block);
		Biome biome = world.getBiome(pos);
		int season = Seasons.GetSeasonForBiome(biome, BetterFarming.SEASON_CLOCK.currentSeason);

		if (crop.CheckConditions(state, (ServerWorld) world, pos, biome, data))
		{
			data.growthTime -= crop.growthRate[season] * estimatedRandomTicksPassed;
		}
		BetterFarming.LOGGER.info("Updated unloaded BlockData");

		// TODO set block to grow or destroy or nothing
		return false;
	}

	public BlockData GetOrCreateEntry(BlockPos pos, Supplier<BlockData> factory)
	{
		return m_blockDataMap.getOrDefault(pos, Create(pos, factory));
	}

	public BlockData GetOrCreateEntry(BlockPos pos, int growthTime, Block block)
	{
		return m_blockDataMap.getOrDefault(pos,
				Create(pos, () -> new BlockData(SeasonManager.GetOrCreate().GetSeasonClock().ticksSinceCreation, growthTime, 0, block)));
	}

	public BlockData Create(BlockPos pos, Supplier<BlockData> factory)
	{
		BlockData data = factory.get();
		m_blockDataMap.put(pos, data);
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
