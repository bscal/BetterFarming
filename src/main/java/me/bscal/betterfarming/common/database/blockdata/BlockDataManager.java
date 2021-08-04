package me.bscal.betterfarming.common.database.blockdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.seasons.SeasonManager;
import me.bscal.betterfarming.common.seasons.SeasonalCrop;
import me.bscal.betterfarming.common.seasons.Seasons;
import me.bscal.betterfarming.common.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

public class BlockDataManager
{

	private Object2ObjectOpenHashMap<WorldPos, BlockData> m_blockDataMap;
	private final ObjectArrayList<World> m_worldCache;

	public BlockDataManager()
	{
		m_worldCache = new ObjectArrayList<>(6);
	}

	public Optional<BlockData> Get(WorldPos worldPos)
	{
		BlockData data = m_blockDataMap.get(worldPos);
		return (data == null) ? Optional.empty() : Optional.of(data);
	}

	public BlockData GetOrCreate(WorldPos worldPos, Supplier<BlockData> factory)
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

	public void UpdateUnloadedEntries(MinecraftServer server, int estimatedRandomTicksPassed)
	{
		if (m_worldCache.size() < 1)
		{
			m_worldCache.add(server.getWorld(World.OVERWORLD));
			m_worldCache.add(server.getWorld(World.NETHER));
			m_worldCache.add(server.getWorld(World.END));
		}
		var entrySet = m_blockDataMap.entrySet();
		for (var pair : entrySet)
		{
			WorldPos worldPos = pair.getKey();
			World world = m_worldCache.stream()
					.filter(w -> w.getRegistryKey().getValue().equals(worldPos.worldId))
					.findFirst()
					.orElse(null);
			if (world == null)
			{
				world = server.getWorld(RegistryKey.of(Registry.WORLD_KEY, worldPos.worldId));
				if (world == null)
				{
					BetterFarming.LOGGER.warn("[ BlockData-Clean ] World " + worldPos.worldId + " was not found... removing");
					entrySet.remove(pair); // Removed
					continue;
				}
				m_worldCache.add(world);
			}

			boolean chunkLoaded = world.getChunkManager()
					.isChunkLoaded(ChunkSectionPos.getSectionCoord(worldPos.pos.getX()),
							ChunkSectionPos.getSectionCoord(worldPos.pos.getZ()));
			if (!chunkLoaded)
			{
				BlockState state = world.getBlockState(worldPos.pos);
				if (state.isAir() || !state.isOf(pair.getValue().block) || UpdateGrowable(worldPos, pair.getValue(), state, world,
						estimatedRandomTicksPassed))
					entrySet.remove(pair); // Removed
			}
		}
	}

	private boolean UpdateGrowable(WorldPos wPos, BlockData data, BlockState state, World world, int estimatedRandomTicksPassed)
	{
		SeasonalCrop crop = BetterFarming.CROP_MANAGER.seasonalCrops.get(data.block);
		Biome biome = world.getBiome(wPos.pos);
		int season = Seasons.GetSeasonForBiome(biome, BetterFarming.SEASON_CLOCK.currentSeason);

		if (crop.CheckConditions(state, (ServerWorld) world, wPos.pos, biome, data))
		{
			data.growthTime -= crop.growthRate[season] * estimatedRandomTicksPassed;
		}
		BetterFarming.LOGGER.info("Updated unloaded BlockData");

		// TODO set block to grow or destroy or nothing
		return false;
	}

	/**
	 * Iterates through all BlockData and removes any non-valid data. This does not update values or check if the block can grow.
	 */
	public void Clean(MinecraftServer server)
	{
		ObjectArrayList<World> worldCache = new ObjectArrayList<>(8);
		worldCache.add(server.getWorld(World.OVERWORLD));
		worldCache.add(server.getWorld(World.NETHER));
		worldCache.add(server.getWorld(World.END));
		var worldRegister = server.getRegistryManager().get(Registry.WORLD_KEY);
		var entrySet = m_blockDataMap.entrySet();
		for (var pair : entrySet)
		{
			World world = worldCache.stream().filter(w -> w.getRegistryKey().getValue() == pair.getKey().worldId).findFirst().orElse(null);
			if (world == null)
			{
				world = worldRegister.get(pair.getKey().worldId);
				if (world == null)
				{
					BetterFarming.LOGGER.warn("[ BlockData-Clean ] World " + pair.getKey().worldId + " was not found... removing");
					entrySet.remove(pair); // Removed
					continue;
				}
				worldCache.add(world);
			}
			BlockState state = world.getBlockState(pair.getKey().pos);
			if (state.isAir() || !state.isOf(pair.getValue().block))
				entrySet.remove(pair); // Removed
		}
	}

	public Object2ObjectOpenHashMap<WorldPos, BlockData> GetDataMap()
	{
		return m_blockDataMap;
	}

	public void Save(String path)
	{
		BetterFarming.LOGGER.info("Saved BlockData");
		//		m_blockDataMap = new Object2ObjectOpenHashMap<>();
		//		BlockData data = new BlockData(1, 3, 5, Blocks.GRASS_BLOCK);
		//		data.destroy = true;
		//		data.grow = true;
		//		m_blockDataMap.put(new WorldPos(World.OVERWORLD.getValue(), new BlockPos(1, 5, 1)), data);
		//		m_blockDataMap.put(new WorldPos(World.NETHER.getValue(), new BlockPos(155, 100, 50)), new BlockData(1000, 2, 10,
		//				Blocks.HAY_BLOCK));
		Utils.WriteJsonToFile(path, GetGsonInstance().toJson(m_blockDataMap));
	}

	public void Load(String path)
	{
		BetterFarming.LOGGER.info("Loaded BlockData");
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
	}
}
