package me.bscal.betterfarming.common.mixin;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTable.class)
public interface LootTableAccessor
{
	@Accessor("pools")
	LootPool[] GetPools();

	@Mutable
	@Accessor("pools")
	void SetPools(LootPool[] pools);

}
