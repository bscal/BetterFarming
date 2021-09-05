package me.bscal.betterfarming.common.seasons;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class SeasonCropManager
{

	private Object2ObjectOpenHashMap<Block, SeasonalCrop> m_seasonalCrops;

	public SeasonCropManager()
	{
		m_seasonalCrops = new Object2ObjectOpenHashMap<>();
	}

	public void Load(String path)
	{
		Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Block.class, new BlockSerializer()).create();
		Type type = new TypeToken<Object2ObjectOpenHashMap<Block, SeasonalCrop>>()
		{
		}.getType();
		try
		{
			JsonReader reader = new JsonReader(new FileReader(path));
			m_seasonalCrops = gson.fromJson(reader, type);
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public Map<Block, SeasonalCrop> GetMap()
	{
		return m_seasonalCrops;
	}

	public boolean Contains(final Block block)
	{
		return m_seasonalCrops.containsKey(block);
	}

	public SeasonalCrop Get(final Block block)
	{
		return m_seasonalCrops.get(block);
	}

	public void Put(final Block block, final SeasonalCrop crop)
	{
		m_seasonalCrops.put(block, crop);
	}

	public static void GenerateDefaults(String path)
	{
		Object2ObjectOpenHashMap<Block, SeasonalCrop> tempCropMap = new Object2ObjectOpenHashMap<>();

		tempCropMap.put(Blocks.WHEAT,
				new SeasonalCrop.Builder(CropBlock.MAX_AGE, (24000 * 3) / SeasonalCrop.RANDOM_TICK_DEFAULT_AVERAGE).SetGrowthRate(1.0f)
						.SetSeasonGrowthRates(1f, 1f, 0f, 0f).SetAgeProp(CropBlock.AGE)
						.Build());
		tempCropMap.put(Blocks.CARROTS,
				new SeasonalCrop.Builder(CropBlock.MAX_AGE, 24000 * 4).SetAgeProp(CropBlock.AGE).SetSeasonGrowthRates(0f, 1f, 0f, 0f).Build());
		tempCropMap.put(Blocks.SUGAR_CANE,
				new SeasonalCrop.Builder(Properties.AGE_15_MAX, 24000 * 1.5f).SetAgeProp(SugarCaneBlock.AGE).SetSeasonGrowthRates(1.0f, 0f).Build());

		Gson gson = new GsonBuilder().setPrettyPrinting()
				.enableComplexMapKeySerialization()
				.registerTypeHierarchyAdapter(Block.class, new BlockSerializer())
				.create();

		Type type = new TypeToken<Object2ObjectOpenHashMap<Block, SeasonalCrop>>()
		{
		}.getType();
		String json = gson.toJson(tempCropMap, type);
		try
		{
			FileWriter writer = new FileWriter(path);
			writer.write(json);
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static class BlockSerializer implements JsonSerializer<Block>, JsonDeserializer<Block>
	{
		@Override
		public Block deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			return Registry.BLOCK.get(Identifier.tryParse(json.getAsString()));
		}

		@Override
		public JsonElement serialize(Block src, Type typeOfSrc, JsonSerializationContext context)
		{
			return new JsonPrimitive(Registry.BLOCK.getId(src).toString());
		}
	}
}
