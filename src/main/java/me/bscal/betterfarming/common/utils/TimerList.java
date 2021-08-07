package me.bscal.betterfarming.common.utils;

import it.unimi.dsi.fastutil.longs.LongArrayList;

import java.math.BigInteger;

public class TimerList
{

	int max;
	LongArrayList entries;

	public TimerList(int expected, int max)
	{
		this.entries = new LongArrayList(expected);
		this.max = max;
	}

	public void Add(long entry)
	{
		if (entries.size() < max)
		{
			entries.add(entry);
		}
	}

	public long Average(boolean clear)
	{
		long res = 0;

		if (entries.size() == 0)
			return res;

		for (long l : entries)
		{
			res += l;
		}

		res /= entries.size();

		if (clear)
			entries.clear();
		return res;
	}

}
