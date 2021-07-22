package me.bscal.betterfarming.mixin.common.world;

import me.bscal.betterfarming.common.seasons.SeasonManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class) public class ServerWorldMixin
{

	@Inject(method = "setTimeOfDay(J)V", at = @At("HEAD"))
	public void OnSetTimeOfDay(long timeOfDay, CallbackInfo ci)
	{
		SeasonManager.GetOrCreate((ServerWorld) (Object) this).Update(timeOfDay);
	}

	//	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
	//	public void OnTickDayNightCycle(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
	//	{
	//		SeasonManager.GetOrCreate((ServerWorld) (Object) this).PassTime();
	//	}
}
