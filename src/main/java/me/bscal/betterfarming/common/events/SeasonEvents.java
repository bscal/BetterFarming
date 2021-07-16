package me.bscal.betterfarming.common.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface SeasonEvents
{

	Event<NewDay> NEW_DAY = EventFactory.createArrayBacked(NewDay.class,
			(listeners) -> (ticks, day) -> {
				for (NewDay listener : listeners)
				{
					listener.OnNewDay(ticks, day);
				}
			});

	Event<SeasonChanged> SEASON_CHANGED = EventFactory.createArrayBacked(SeasonChanged.class, listeners -> (newSeason, currentDay) ->
	{
		for (SeasonChanged listener : listeners)
		{
			listener.OnSeasonChanged(newSeason, currentDay);
		}
	});


	@FunctionalInterface interface NewDay
	{
		void OnNewDay(long ticks, long day);
	}

	@FunctionalInterface interface SeasonChanged
	{
		void OnSeasonChanged(int newSeason, long currentDay);
	}

}
