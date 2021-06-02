package me.bscal.betterfarming.common.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

import java.util.Map;

public interface LootManagerEarlyAssignCallback
{

	Event<LootManagerEarlyAssignCallback> EARLY_ASSIGN = EventFactory.createArrayBacked(
			LootManagerEarlyAssignCallback.class,
			(listeners) -> (tables, manager, lootManager) -> {
				for (LootManagerEarlyAssignCallback listener : listeners) {
					TypedActionResult<Map<Identifier, LootTable>> result = listener.OnEarlyApply(tables, manager, lootManager);

					if(result.getResult() != ActionResult.PASS) {
						return result;
					}
				}

				return TypedActionResult.pass(tables);
			});

	TypedActionResult<Map<Identifier, LootTable>> OnEarlyApply(Map<Identifier, LootTable> tables, ResourceManager manager, LootManager lootManager);
}
