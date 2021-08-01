package me.bscal.betterfarming.common.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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

	public static int Clamp(int value, int min, int max)
	{
		return Math.min(max, Math.max(min, value));
	}

	public static double Clamp(double value, double min, double max)
	{
		return Math.min(max, Math.max(min, value));
	}

}
