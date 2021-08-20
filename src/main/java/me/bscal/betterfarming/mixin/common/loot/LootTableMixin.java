package me.bscal.betterfarming.mixin.common.loot;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin
{

	@Inject(method = "generateLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "HEAD"))
	public void OnGenerateLoot(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci)
	{

	}

	private void GenerateLoot()
	{

	}

}
