package me.bscal.betterfarming.common.loot.override.system;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class EntityLootContext extends LootContext
{

	public @NotNull Entity entity;
	public @NotNull Identifier entityId;
	public @NotNull ServerWorld world;
	public @NotNull net.minecraft.loot.context.LootContext minecraftLootContext;
	public @NotNull net.minecraft.loot.LootTable minecraftLootTable;
	public @NotNull DamageSource source;
	public boolean causedByPlayer;

	public EntityLootContext(@NotNull Entity entity, @NotNull Identifier entityId, @NotNull ServerWorld world,
			@NotNull net.minecraft.loot.context.LootContext minecraftLootContext, @NotNull  net.minecraft.loot.LootTable minecraftLootTable, @NotNull DamageSource source,
			boolean causedByPlayer, LootTable overrideLootTable)
	{
		super(overrideLootTable);
		this.entity = entity;
		this.entityId = entityId;
		this.world = world;
		this.minecraftLootContext = minecraftLootContext;
		this.minecraftLootTable = minecraftLootTable;
		this.source = source;
		this.causedByPlayer = causedByPlayer;
	}
}
