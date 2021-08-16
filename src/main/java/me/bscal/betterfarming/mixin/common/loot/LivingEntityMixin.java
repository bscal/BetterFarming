package me.bscal.betterfarming.mixin.common.loot;

import me.bscal.betterfarming.common.loot.override.LootOverrideManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(LivingEntity.class) public abstract class LivingEntityMixin extends Entity
{

	public LivingEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Inject(method = "dropLoot", at = @At(value = "INVOKE", target =
			"Lnet/minecraft/loot/LootTable;generateLoot" + "(Lnet/minecraft/loot" + "/context/LootContext;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
	public void OnDropLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci, Identifier identifier, LootTable lootTable,
			LootContext.Builder builder)
	{
		LivingEntity ent = (LivingEntity) (Object) this;
		Identifier entityId = Registry.ENTITY_TYPE.getId(this.getType());

		LootContext context = builder.build(LootContextTypes.ENTITY);

		List<ItemStack> loot = LootOverrideManager.Get()
				.RunLootableEntity(entityId, ent, context, source, causedByPlayer, identifier, lootTable);

		if (loot != null)
		{
			LootOverrideManager.Get().DropEntityLoot(loot, ent, 0.0f);
			ci.cancel();
		}

	}
}
