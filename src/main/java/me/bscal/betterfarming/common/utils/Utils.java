package me.bscal.betterfarming.common.utils;

public final class Utils
{

	public static int Clamp(int value, int min, int max)
	{
		return Math.min(max, Math.max(min, value));
	}

	public static double Clamp(double value, double min, double max)
	{
		return Math.min(max, Math.max(min, value));
	}

}
