package me.bscal.betterfarming.common.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import me.bscal.betterfarming.common.seasons.SeasonalCrop;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public final class Utils
{

	public static File GetFileInConfig(String fileName, String... subDirs)
	{
		StringBuilder sb = new StringBuilder();
		for (String str : subDirs)
		{
			sb.append(str).append(File.pathSeparatorChar);
		}
		sb.append(fileName);

		return new File(FabricLoader.getInstance().getConfigDir().toFile(), sb.toString());
	}

	public static Path GetPathInConfig(String childPath)
	{
		return Paths.get(FabricLoader.getInstance().getConfigDir().toString(), childPath);
	}

	public static String GetStringPathInConfig(String childPath)
	{
		return GetPathInConfig(childPath).toString();
	}

	public static int Clamp(int value, int min, int max)
	{
		return Math.min(max, Math.max(min, value));
	}

	public static double Clamp(double value, double min, double max)
	{
		return Math.min(max, Math.max(min, value));
	}

	public static Vec3i Vec3iFromShortString(String vec3iString)
	{
		vec3iString = vec3iString.replace(" ", "");
		String[] split = vec3iString.split(",");
		return new Vec3i(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	}

	public static void WriteJsonToFile(String path, String json)
	{
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

	public static <T> T ReadJsonFromFile(String path, Gson gson, Type type)
	{
		T result = null;
		try
		{
			JsonReader reader = new JsonReader(new FileReader(path));
			result = gson.fromJson(reader, type);
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}
}
