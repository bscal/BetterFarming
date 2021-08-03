package me.bscal.betterfarming.common.database.blockdata;

import com.google.gson.*;
import me.bscal.betterfarming.common.utils.Utils;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Type;

public class BlockData
{

	public long creationTime;
	public int growthTime;
	public int age;

	public BlockData()
	{
	}

	public BlockData(long creationTime, int growthTime, int age)
	{
		this.creationTime = creationTime;
		this.growthTime = growthTime;
		this.age = age;
	}

	public static class Serialize implements JsonSerializer<BlockData>, JsonDeserializer<BlockData>
	{
		@Override
		public BlockData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if (json instanceof JsonArray array)
				return new BlockData(array.get(0).getAsLong(), array.get(1).getAsInt(), array.get(2).getAsInt());
			return new BlockData();
		}

		@Override
		public JsonElement serialize(BlockData src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonArray array = new JsonArray();
			array.add(src.creationTime);
			array.add(src.growthTime);
			array.add(src.age);
			return array;
		}
	}

}
