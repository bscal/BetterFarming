package me.bscal.betterfarming.common.loot.override.system;

public record LootDrop(float chance, boolean unique, boolean alwaysDrop, boolean isEnabled, LootItem item)
{
}
