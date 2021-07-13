package me.bscal.betterfarming.seasons;

import me.bscal.betterfarming.common.seasons.SeasonManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinecraftDateTests
{

	@Test
	public void TestCanMinecraftDateParseFromTicks()
	{
		// age + year * 1234 + month * 28 + day * 31
		SeasonManager.MinecraftDate minecraftDate = new SeasonManager.MinecraftDate(19322664000L);
		assertEquals(minecraftDate.day, 1L);
		assertEquals(minecraftDate.month, 5L);
		assertEquals(minecraftDate.year, 236L);
		assertEquals(minecraftDate.age, 2L);
		assertEquals(minecraftDate.remainingTicks, 0L);
	}

	@Test
	public void TestCanMinecraftDateParseSmallParse()
	{
		SeasonManager.MinecraftDate minecraftDate = new SeasonManager.MinecraftDate(24000L * 59L + 23999L);
		assertEquals(minecraftDate.day, 29L);
		assertEquals(minecraftDate.month, 1L);
		assertEquals(minecraftDate.year, 0L);
		assertEquals(minecraftDate.age, 0L);
		assertEquals(minecraftDate.remainingTicks, 23999L);
	}

	@Test
	public void TestCanMinecraftDateParseLargeParse()
	{
		SeasonManager.MinecraftDate minecraftDate = new SeasonManager.MinecraftDate(5111141513454775807L);
		assertEquals(minecraftDate.day, 2L);
		assertEquals(minecraftDate.month, 0L);
		assertEquals(minecraftDate.year, 798L);
		assertEquals(minecraftDate.age, 591567304L);
		assertEquals(minecraftDate.remainingTicks, 7807L);
	}

	@Test
	public void TestCanMinecraftDateParseHoursAndMinute()
	{
		int[] hoursAndMinutes = SeasonManager.MinecraftDate.ParseHourAndMinutesFromTicks(16523);
		assertEquals(hoursAndMinutes[0], 22);
		assertEquals(hoursAndMinutes[1], 31);

		hoursAndMinutes = SeasonManager.MinecraftDate.ParseHourAndMinutesFromTicks(0);
		assertEquals(hoursAndMinutes[0], 6);
		assertEquals(hoursAndMinutes[1], 0);
	}

}
