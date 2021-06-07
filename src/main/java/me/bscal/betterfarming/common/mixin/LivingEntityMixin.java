package me.bscal.betterfarming.common.mixin;

import me.bscal.betterfarming.common.components.entity.EntityEcoProvider;
import me.bscal.betterfarming.common.components.entity.IEntityEcoComponent;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{

	@Inject(method = "tick", at = @At(value = "TAIL"))
	public void OnTick(CallbackInfo ci)
	{
		LivingEntity entity = (LivingEntity) (Object) this;
	}

}
