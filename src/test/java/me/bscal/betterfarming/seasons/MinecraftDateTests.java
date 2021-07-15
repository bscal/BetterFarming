package me.bscal.betterfarming.seasons;

import me.bscal.betterfarming.common.seasons.MinecraftDate;
import me.bscal.betterfarming.common.seasons.SeasonManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinecraftDateTests
{

	@Test
	public void TestCanMinecraftDateParseFromTicks()
	{
		// age + year * 1234 + month * 28 + day * 31
		MinecraftDate minecraftDate = new MinecraftDate(19322664000L);
		assertEquals(minecraftDate.day, 1L);
		assertEquals(minecraftDate.month, 5L);
		assertEquals(minecraftDate.year, 236L);
		assertEquals(minecraftDate.age, 2L);
		assertEquals(minecraftDate.remainingTicks, 0L);
	}

	@Test
	public void TestCanMinecraftDateParseSmallParse()
	{
		MinecraftDate minecraftDate = new MinecraftDate(24000L * 59L + 23999L);
		assertEquals(minecraftDate.day, 29L);
		assertEquals(minecraftDate.month, 1L);
		assertEquals(minecraftDate.year, 0L);
		assertEquals(minecraftDate.age, 0L);
		assertEquals(minecraftDate.remainingTicks, 23999L);
	}

	@Test
	public void TestCanMinecraftDateParseLargeParse()
	{
		MinecraftDate minecraftDate = new MinecraftDate(5111141513454775807L);
		assertEquals(minecraftDate.day, 2L);
		assertEquals(minecraftDate.month, 0L);
		assertEquals(minecraftDate.year, 798L);
		assertEquals(minecraftDate.age, 591567304L);
		assertEquals(minecraftDate.remainingTicks, 7807L);
	}

	@Test
	public void TestCanMinecraftDateParseHoursAndMinute()
	{
		int[] hoursAndMinutes = MinecraftDate.ParseHourAndMinutesFromTicks(16523);
		assertEquals(22, hoursAndMinutes[0]);
		assertEquals(31, hoursAndMinutes[1]);

		hoursAndMinutes = MinecraftDate.ParseHourAndMinutesFromTicks(0);
		assertEquals(6, hoursAndMinutes[0]);
		assertEquals(0, hoursAndMinutes[1]);
	}

	@Test
	public void TestMinecraftDateNumberSuffix()
	{
		int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 21, 52, 73, 99, 100};
		String[] expectedSuffixs = {"st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "st", "nd", "rd", "th", "th"};

		for (int i = 0 ; i < values.length; i++)
		{
			String foundSuffix = MinecraftDate.StringFindNumberSuffix(values[i]);
			assertEquals(expectedSuffixs[i], foundSuffix, "Asserted at index: " + i);
		}
	}


}
