package me.bscal.betterfarming.common.seasons;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.text.TranslatableText;

public class MinecraftDate
{
	public static final String LANG_PATH = "mcdates." + BetterFarming.MOD_ID + ".";
	public static final long TICKS_PER_DAY = 24000;
	public static final long TICKS_PER_MONTH = 30 * TICKS_PER_DAY;
	public static final long TICKS_PER_YEAR = 12 * TICKS_PER_MONTH;
	public static final long TICKS_PER_AGE = 1000 * TICKS_PER_YEAR;

	public int day;
	public int month;
	public int year;
	public long age;
	public int remainingTicks;

	public MinecraftDate()
	{
	}

	public MinecraftDate(long ticks)
	{
		ParseDateFromTickTime(ticks);
	}

	public void ParseDateFromTickTime(long ticks)
	{
		age = Math.floorDiv(ticks, TICKS_PER_AGE);
		ticks -= age * TICKS_PER_AGE;

		year = (int) Math.floorDiv(ticks, TICKS_PER_YEAR);
		ticks -= year * TICKS_PER_YEAR;

		month = (int) Math.floorDiv(ticks, TICKS_PER_MONTH);
		ticks -= month * TICKS_PER_MONTH;

		day = (int) Math.floorDiv(ticks, TICKS_PER_DAY);
		ticks -= day * TICKS_PER_DAY;

		remainingTicks = (int) ticks;
	}

	public String AsDateShort(boolean includeAge)
	{
		TranslatableText dateText = new TranslatableText(LANG_PATH + "date_short", day, month, year);
		return (includeAge) ? dateText.asString() + GetAgeStringShort(age) : dateText.asString();
	}

	public String AsDate(boolean includeAge)
	{
		TranslatableText dateText = new TranslatableText(LANG_PATH + "date", GetMonthString(month), day,
				year);
		return (includeAge) ? dateText.asString() + GetAgeString(age) : dateText.asString();
	}

	/**
	 * Parses ticks into minecraft time. 6:00 is 0 ticks. Uses 24-hour clock.
	 *
	 * @return Int array of size 2 with the hours and minutes. Index 0 = hours. Index 1 = minutes.
	 */
	public static int[] ParseHourAndMinutesFromTicks(int ticks)
	{
		int[] hoursAndMinutes = new int[2];
		ticks = Math.min(ticks, 24000);

		int hours = Math.floorDiv(ticks, 1000);
		int hoursTime = 6 + hours; // Minecraft time starts at 6:00
		hoursAndMinutes[0] = (hoursTime > 23) ? hoursTime - 24 : hoursTime;
		ticks -= hours * 1000;

		hoursAndMinutes[1] = (int) Math.floor((float) ticks / 16.6f);

		return hoursAndMinutes;
	}

	public static String StringFindNumberSuffix(int value)
	{
		int modNum = value % 10;
		if (modNum == 1 && value != 11)
			return "st";
		else if (modNum == 2 && value != 12)
			return "nd";
		else if (modNum == 3 && value != 13)
			return "rd";
		else
			return "th";
	}

	public static String StringFindNumberSuffix(long value)
	{
		long modNum = value % 10;
		if (modNum == 1 && value != 11)
			return "st";
		else if (modNum == 2 && value != 12)
			return "nd";
		else if (modNum == 3 && value != 13)
			return "rd";
		else
			return "th";
	}

	public static String GetMonthString(int month)
	{
		if (month < 13 && month > 0)
			return new TranslatableText(LANG_PATH + "m" + month).asString();
		return "Month " + month;
	}

	public static String GetAgeStringShort(long age)
	{
		String ageString;
		if (age < 11 && age > -1)
			ageString = new TranslatableText(LANG_PATH + "age_s" + age).asString();
		else
			ageString = new TranslatableText(LANG_PATH + "age_short", age,
					StringFindNumberSuffix(age)).asString();
		return ageString;
	}

	public static String GetAgeString(long age)
	{
		String ageString;
		if (age < 11 && age > -1)
			ageString = new TranslatableText(LANG_PATH + "age_l" + age).asString();
		else
			ageString = new TranslatableText(LANG_PATH + "age_normal", age,
					StringFindNumberSuffix(age)).asString();
		return ageString;
	}
}
