package me.bscal.betterfarming.common.config;

import com.google.gson.Gson;

public abstract class ConfigBlock
{

	/**
	 * Used for internal use to identify ConfigBlocks and used as json key
	 */
	private String __name;

	protected ConfigBlock()
	{
	}

	public ConfigBlock(String name)
	{
		this.__name = name;
	}

	public String GetName()
	{
		return __name;
	}

	/**
	 * A simple json clone method. Cloneable seems kinda silly when gson infrastructure is already here
	 */
	public ConfigBlock Clone()
	{
		Gson gson = new Gson();
		String tmpJson = gson.toJson(this, getClass());
		ConfigBlock block = gson.fromJson(tmpJson, getClass());
		block.__name = this.__name;
		return block;
	}
}
