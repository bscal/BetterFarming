package me.bscal.betterfarming.common.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import me.bscal.betterfarming.common.events.LootManagerEarlyAssignCallback;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = LootManager.class, priority = 900) public class LootManagerMixin
{

	@Shadow private Map<Identifier, LootTable> tables;

	@Inject(method = "apply", at = @At("RETURN"))
	private void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler,
			CallbackInfo ci)
	{
		LootManager lootManager = (LootManager) (Object) this;
		tables = ImmutableMap.copyOf(
				LootManagerEarlyAssignCallback.EARLY_ASSIGN.invoker().OnEarlyApply(tables, resourceManager,
						lootManager).getValue());
	}

}
