package me.bscal.betterfarming.common.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Config
{

	private final String m_path;
	private final Gson m_gson;
	private final List<ConfigBlock> m_defaults;
	private List<ConfigBlock> m_blocks;

	/**
	 * Creates a new config to the desired path. If file does not exists, a new one with the defaults will be created.
	 * If the file does exist then will attempt to deserialize the config.
	 * <br>
	 * If you'd like to customize Gson instance you can override <code>ExtendGsonBuilder()</code> this will allow the
	 * internal settings for gson to remain but allow you to add your own <code>TypeAdapters</code>.
	 */
	public Config(String path, ConfigBlock... blocks)
	{
		this.m_path = path;
		ExclusionStrategy exclusionStrategy = new ExcludeConfigBlockName();
		this.m_gson = ExtendGsonBuilder(new GsonBuilder().setPrettyPrinting()
				.addSerializationExclusionStrategy(exclusionStrategy)
				.addDeserializationExclusionStrategy(exclusionStrategy)).create();
		int size = blocks.length == 0 ? 4 : blocks.length;
		this.m_defaults = new ArrayList<>(size);
		this.m_blocks = new ArrayList<>(size);

		List<ConfigBlock> allBlocks = Arrays.asList(blocks);
		allBlocks.addAll(AddNewConfigBlocks());
		allBlocks.forEach(this::InsertConfigBlock);

		if (!CreateDefaultsIfNotExist())
		{
			Deserialize();
		}
	}

	/*
		Simple method if you decide to extend Config you can set Configs before Deserialization in the constructor occurs.
	 */
	protected List<ConfigBlock> AddNewConfigBlocks()
	{
		return new ArrayList<>(0);
	}

	protected void InsertConfigBlock(ConfigBlock block)
	{
		m_defaults.add(block);
		m_blocks.add(block.Clone());
	}

	public GsonBuilder ExtendGsonBuilder(GsonBuilder gsonBuilder)
	{
		// In case you didn't want this setting you can override this method.
		return gsonBuilder.enableComplexMapKeySerialization();
	}

	/**
	 *	If file to Config's path does not exist will create and return true. Returns false if it does exist.
	 */
	public boolean CreateDefaultsIfNotExist()
	{
		if (!new File(m_path).exists())
		{
			Serialize();
			return true;
		}
		else
		{
			return false;
		}
	}

	public void ResetToDefaults()
	{
		List<ConfigBlock> list = new ArrayList<>(m_defaults.size());
		m_defaults.forEach((configBlock -> list.add(configBlock.Clone())));
		m_blocks = list;
		Serialize();
	}

	public void Serialize()
	{
		try
		{
			FileWriter writer = new FileWriter(m_path, false);
			JsonWriter jsonWriter = new JsonWriter(writer);

			Map<String, ConfigBlock> keyValueMapForJson = new HashMap<>(m_blocks.size());

			for (ConfigBlock block : m_blocks)
			{
				keyValueMapForJson.put(block.GetName(), block);
			}
			jsonWriter.jsonValue(m_gson.toJson(keyValueMapForJson));

			writer.flush();
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void Deserialize()
	{
		try
		{
			FileReader reader = new FileReader(m_path);
			JsonReader jsonReader = new JsonReader(reader);
			JsonParser parser = new JsonParser();

			JsonObject root = parser.parse(jsonReader).getAsJsonObject();
			var entries = root.entrySet();
			for (var pair : entries)
			{
				int index = FindIndex(pair.getKey());
				if (index >= 0)
				{
					var newBlock = m_gson.fromJson(pair.getValue(), m_blocks.get(index).getClass());
					m_blocks.set(index, newBlock);
				}
			}

			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private int FindIndex(String blockName)
	{
		for (int i = 0; i < m_blocks.size(); i++)
		{
			if (m_blocks.get(i).GetName().equals(blockName))
			{
				return i;
			}
		}
		return -1;
	}

	protected static class ExcludeConfigBlockName implements ExclusionStrategy
	{

		@Override
		public boolean shouldSkipField(FieldAttributes f)
		{
			return f.getName().equals("__name");
		}

		@Override
		public boolean shouldSkipClass(Class<?> clazz)
		{
			return false;
		}
	}
}