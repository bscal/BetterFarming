package me.bscal.betterfarming.common.seasons;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SeasonCropManager
{

	public Map<Block, SeasonalCrop> seasonalCrops = new HashMap<>();

	public SeasonCropManager()
	{
	}

	public void Load(String path)
	{
		Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Block.class, new BlockSerializer()).create();
		Type type = new TypeToken<Map<Block, SeasonalCrop>>()
		{
		}.getType();
		try
		{
			JsonReader reader = new JsonReader(new FileReader(path));
			seasonalCrops = gson.fromJson(reader, type);
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void GenerateDefaults(String path)
	{
		Map<Block, SeasonalCrop> tempCropMap = new HashMap<>();

		tempCropMap.put(Blocks.WHEAT, new SeasonalCrop.Builder().SetGrowRates(1f, 1f, .5f, 0f).Build());
		tempCropMap.put(Blocks.CARROTS, new SeasonalCrop.Builder().SetGrowRates(0f, 1f, 0f, 0f).Build());
		tempCropMap.put(Blocks.SUGAR_CANE, new SeasonalCrop.Builder().SetGrowRates(1f, 0f).Build());

		Gson gson = new GsonBuilder().setPrettyPrinting()
				.enableComplexMapKeySerialization()
				.registerTypeHierarchyAdapter(Block.class, new BlockSerializer())
				.create();

		Type type = new TypeToken<Map<Block, SeasonalCrop>>()
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
