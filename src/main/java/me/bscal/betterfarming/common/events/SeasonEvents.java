package me.bscal.betterfarming.common.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface SeasonEvents
{

	Event<SeasonEvents> NEW_DAY = EventFactory.createArrayBacked(SeasonEvents.class,
			(listeners) -> (ticks, day, fromTimePass) -> {
				for (SeasonEvents listener : listeners)
				{
					ActionResult result = listener.NewDay(ticks, day, fromTimePass);
					if (result != ActionResult.PASS)
					{
						return result;
					}
				}
				return ActionResult.PASS;
			});

	ActionResult NewDay(long ticks, long day, boolean fromTimePass);

}
