package me.bscal.betterfarming.common.database.blockdata;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.bscal.betterfarming.common.seasons.SeasonManager;
import me.bscal.betterfarming.common.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Type;
import java.util.Optional;

public class BlockDataManager
{

	private Object2ObjectOpenHashMap<WorldPos, BlockData> m_blockDataMap;

	public BlockDataManager()
	{
	}

	public Optional<BlockData> Get(WorldPos worldPos)
	{
		BlockData data = m_blockDataMap.get(worldPos);
		return (data == null) ? Optional.empty() : Optional.of(data);
	}

	public BlockData GetOrCreate(WorldPos worldPos)
	{
		return m_blockDataMap.getOrDefault(worldPos, Create(worldPos));
	}

	public BlockData Create(WorldPos worldPos)
	{
		BlockData data = new BlockData(SeasonManager.GetOrCreate().GetSeasonClock().ticksSinceCreation, 0, 0);
		m_blockDataMap.put(worldPos, data);
		return data;
	}

	public void Remove(WorldPos worldPos)
	{
		BlockData data = m_blockDataMap.remove(worldPos);
		data = null;
	}

	public Object2ObjectOpenHashMap<WorldPos, BlockData> GetDataMap()
	{
		return m_blockDataMap;
	}

	public void Save(String path)
	{
		m_blockDataMap = new Object2ObjectOpenHashMap<>();
		m_blockDataMap.put(new WorldPos(World.OVERWORLD.getValue(), new BlockPos(1, 5, 1)), new BlockData(1, 3, 5));
		m_blockDataMap.put(new WorldPos(World.NETHER.getValue(), new BlockPos(155, 100, 50)), new BlockData(1000, 2, 10));
		Utils.WriteJsonToFile(path, GetGsonInstance().toJson(m_blockDataMap));
	}

	public void Load(String path)
	{
		Type type = new TypeToken<Object2ObjectOpenHashMap<WorldPos, BlockData>>()
		{
		}.getType();
		m_blockDataMap = Utils.ReadJsonFromFile(path, GetGsonInstance(), type);
		m_blockDataMap.defaultReturnValue(new BlockData(-1, -1, -1));
	}

	private Gson GetGsonInstance()
	{
		return new GsonBuilder().registerTypeHierarchyAdapter(Object2ObjectOpenHashMap.class, new Serialize()).create();
	}

	public static class Serialize implements JsonSerializer<Object2ObjectOpenHashMap<WorldPos, BlockData>>,
			JsonDeserializer<Object2ObjectOpenHashMap<WorldPos, BlockData>>
	{

		@Override
		public Object2ObjectOpenHashMap<WorldPos, BlockData> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context)
				throws JsonParseException
		{
			return null;
		}

		@Override
		public JsonElement serialize(Object2ObjectOpenHashMap<WorldPos, BlockData> src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject object = new JsonObject();

			// TODO probably not the best way to handle but easiest
			var serializer = new BlockData.Serialize();

			for (var pair : src.object2ObjectEntrySet())
			{
				object.add(pair.getKey().toString(), serializer.serialize(pair.getValue(), typeOfSrc, context));
			}

			return object;
		}
	}

}
