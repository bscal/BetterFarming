package me.bscal.betterfarming.common.utils.schedulers;

import net.minecraft.nbt.NbtCompound;

import java.io.*;
import java.util.Base64;

public abstract class PersistentSchedulable implements Schedulable, Serializable
{

	public void Serialize(NbtCompound nbt)
	{
		nbt.putString("schedulable", Utils.ToString(this));
	}

	public static PersistentSchedulable Deserialize(NbtCompound nbt)
	{
		if (!nbt.contains("schedulable"))
			return null;
		return (PersistentSchedulable) Utils.FromString(nbt.getString("schedulable"));
	}
}
