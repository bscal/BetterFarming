package me.bscal.betterfarming.common.loot.override.system;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record LootItem<T>(@NotNull T stack, @Nullable Function<LootContext, T> bonusModifier)
{

}
