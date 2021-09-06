package me.bscal.betterfarming.common.database.blockdata;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

public class BlockData implements IBlockDataBlock
{

	public long lastUpdate;
	public int growthTime;
	public int age;
	public Block block;
	public boolean ableToGrow;

	public BlockData()
	{
	}

	public BlockData(long lastUpdate, int growthTime, int age, Block block)
	{
		this(lastUpdate, growthTime, age, block, false);
	}

	public BlockData(long lastUpdate, int growthTime, int age, Block block, boolean ableToGrow)
	{
		this.lastUpdate = lastUpdate;
		this.growthTime = growthTime;
		this.age = age;
		this.block = block;
		this.ableToGrow = ableToGrow;
	}

	/**
	 * Keeping incase addition flags/data will be used?
	 */
	private BlockData SetFlags(int bits)
	{
		//grow = (bits & 1) != 0;
		//destroy = (bits & 1 << 1) != 0;
		return this;
	}

	@Override
	public NbtCompound ToNbt(NbtCompound nbt)
	{
		nbt.putLong("lastUpdate", lastUpdate);
		nbt.putInt("growthTime", growthTime);
		nbt.putInt("age", age);
		nbt.putString("block", String.valueOf(Registry.BLOCK.getId(block)));
		nbt.putBoolean("ableToGrow", ableToGrow);
		//int bits = 0x00000000;
		//bits |= ((src.grow) ? 1 : 0);
		//bits |= ((src.destroy) ? 1 : 0) << 1;
		//nbt.putInt("flags", bits);
		return nbt;
	}

	@Override
	public void FromNbt(NbtCompound nbt)
	{
		lastUpdate = nbt.getLong("lastUpdate");
		growthTime = nbt.getInt("growthTime");
		age = nbt.getInt("age");
		block = Registry.BLOCK.get(Identifier.tryParse(nbt.getString("block")));
		ableToGrow = nbt.getBoolean("ableToGrow");
	}

	@Override
	public Block GetBlock()
	{
		return null;
	}

	public static class Serializer implements JsonSerializer<BlockData>, JsonDeserializer<BlockData>
	{

		@Override
		public BlockData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if (json instanceof JsonArray array)
			{
				Block block = Registry.BLOCK.get(Identifier.tryParse(array.get(3).getAsString()));
				return new BlockData(array.get(0).getAsLong(), array.get(1).getAsInt(), array.get(2).getAsInt(), block).SetFlags(
						array.get(4).getAsInt());
			}
			return new BlockData();
		}

		@Override
		public JsonElement serialize(BlockData src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonArray array = new JsonArray();
			array.add(src.lastUpdate);
			array.add(src.growthTime);
			array.add(src.age);
			array.add(String.valueOf(Registry.BLOCK.getId(src.block)));

			array.add(src.ableToGrow);
			//int bits = 0x00000000;
			//bits |= ((src.grow) ? 1 : 0);
			//bits |= ((src.destroy) ? 1 : 0) << 1;
			//array.add(bits);

			return array;
		}

		public static BlockData deserializeNbt(NbtCompound nbt)
		{
			return new BlockData(nbt.getLong("lastUpdate"), nbt.getInt("growthTime"), nbt.getInt("age"),
					Registry.BLOCK.get(Identifier.tryParse(nbt.getString("block"))), nbt.getBoolean("ableToGrow"));
		}

		public static NbtCompound serializeNbt(BlockData src, NbtCompound nbt)
		{
			nbt.putLong("lastUpdate", src.lastUpdate);
			nbt.putInt("growthTime", src.growthTime);
			nbt.putInt("age", src.age);
			nbt.putString("block", String.valueOf(Registry.BLOCK.getId(src.block)));
			nbt.putBoolean("ableToGrow", src.ableToGrow);
			//int bits = 0x00000000;
			//bits |= ((src.grow) ? 1 : 0);
			//bits |= ((src.destroy) ? 1 : 0) << 1;
			//nbt.putInt("flags", bits);
			return nbt;
		}
	}

}
