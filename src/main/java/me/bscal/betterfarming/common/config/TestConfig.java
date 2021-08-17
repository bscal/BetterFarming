package me.bscal.betterfarming.common.config;

import com.google.gson.GsonBuilder;
import me.bscal.betterfarming.common.database.blockdata.WorldPos;

public class TestConfig extends Config
{
	/**
	 * Creates a new config to the desired path. If file does not exists, a new one with the defaults will be created.
	 * If the file does exist then will attempt to deserialize the config.
	 * <br>
	 * If you'd like to customize Gson instance you can override <code>ExtendGsonBuilder()</code> this will allow the
	 * internal settings for gson to remain but allow you to add your own <code>TypeAdapters</code>.
	 *
	 * @param path
	 * @param blocks
	 */
	public TestConfig(String path, ConfigBlock... blocks)
	{
		super(path, blocks);
	}

	@Override
	public GsonBuilder ExtendGsonBuilder(GsonBuilder gsonBuilder)
	{
		return super.ExtendGsonBuilder(gsonBuilder).registerTypeAdapter(WorldPos.class, new WorldPos.Serializer());
	}
}
