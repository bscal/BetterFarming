package me.bscal.betterfarming.mixin.common.loot;

import me.bscal.betterfarming.common.loot.lootapi.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(LivingEntity.class) public abstract class LivingEntityMixin extends Entity
{

	@Shadow
	public abstract Identifier getLootTable();

	public LivingEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Inject(method = "dropLoot", at = @At(value = "INVOKE", target =
			"Lnet/minecraft/loot/LootTable;generateLoot" + "(Lnet/minecraft/loot" + "/context/LootContext;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
	public void OnDropLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci, Identifier identifier,
			net.minecraft.loot.LootTable minecraftLootTable, LootContext.Builder builder)
	{
		LivingEntity ent = (LivingEntity) (Object) this;

		if (identifier != LootTables.EMPTY)
		{
			LootTable lootTable = LootRegistry.GetLootTable(identifier);

			if (lootTable != null)
			{
				Identifier entityId = Registry.ENTITY_TYPE.getId(this.getType());
				LootContext context = builder.build(LootContextTypes.ENTITY);
				ServerWorld serverWorld = context.getWorld();

				EntityLootContext entityContext = new EntityLootContext(ent, entityId, serverWorld, context, minecraftLootTable, source,
						causedByPlayer, lootTable);
				List<LootDrop> drops = lootTable.Roll(entityContext, 0);
				if (drops == null || drops.isEmpty())
				{
					// Returns an empty list because we want to override loot
					if (lootTable.alwaysOverrideDefault)
						ci.cancel();
				}
				else
				{
					LootTable.ProcessItemDrop(drops, entityContext, ent);
					// Returns an empty list because we want to override loot
					ci.cancel();
				}
			}
		}
		// Will continue and use default loot generation
	}
}
