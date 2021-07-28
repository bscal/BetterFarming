package me.bscal.betterfarming.common.seasons;

public class SeasonClock
{
	public int currentSeason;
	public int ticksInCurrentSeason;
	public long ticksSinceCreation;

	public boolean IsLateInSeason()
	{
		return ticksInCurrentSeason < 1;
	}
}
