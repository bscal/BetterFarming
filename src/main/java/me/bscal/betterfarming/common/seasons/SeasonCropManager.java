package me.bscal.betterfarming.common.seasons;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import me.bscal.betterfarming.common.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SeasonCropManager
{

	public Map<Identifier, SeasonalCrop> seasonalCrops = new HashMap<>();

	public SeasonCropManager()
	{
	}

	public void Load(String path)
	{
		Gson gson = new GsonBuilder().registerTypeAdapter(Identifier.class, new Identifier.Serializer()).create();
		Type type = new TypeToken<Map<Identifier, SeasonalCrop>>()
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
		Map<Identifier, SeasonalCrop> tempCropMap = new HashMap<>();

		tempCropMap.put(Registry.BLOCK.getId(Blocks.WHEAT), new SeasonalCrop.Builder().SetGrowRates(1f, 1f, .5f, 0f).Build());

		Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Identifier.class, new Identifier.Serializer()).create();
		String json = gson.toJson(tempCropMap);
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

	public static class SeasonalCrop
	{
		public float growRate[];

		public static class Builder
		{
			private float growRate[];

			public Builder SetGrowRates(float... growRates)
			{
				this.growRate = growRates;
				return this;
			}

			public SeasonalCrop Build()
			{
				SeasonalCrop crop = new SeasonalCrop();
				crop.growRate = growRate;
				return crop;
			}
		}
	}
}
