package me.bscal.betterfarming.common.utils.schedulers;

import net.minecraft.nbt.NbtCompound;

import java.io.*;
import java.util.Base64;

public abstract class PersistentSchedulable implements Schedulable, Serializable
{

	public void Serialize(NbtCompound nbt)
	{
		nbt.putString("scheduleable", ToString(this));
	}

	public static PersistentSchedulable Deserialize(NbtCompound nbt)
	{
		if (!nbt.contains("scheduleable"))
			return null;
		return (PersistentSchedulable) FromString(nbt.getString("scheduleable"));
	}

	/**
	 * Read the object from Base64 string.
	 */
	public static Object FromString(String s)
	{
		byte[] data = Base64.getDecoder().decode(s);
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			return o;
		}
		catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Write the object to a Base64 string.
	 */
	public static String ToString(Serializable o)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.close();
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
