package me.bscal.betterfarming.common.loot.lootapi;

public record LootDrop(float chance, boolean unique, boolean alwaysDrop, boolean isEnabled, LootItem<?> item)
{
}