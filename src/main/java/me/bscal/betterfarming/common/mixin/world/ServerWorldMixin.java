package me.bscal.betterfarming.common.mixin.world;

import me.bscal.betterfarming.common.seasons.SeasonManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerWorldMixin
{

	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
	public void OnTickDayNightCycle(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
	{
		SeasonManager.GetOrCreate((ServerWorld) (Object) this).PassTime();
	}

//	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;wakeSleepingPlayers()V"))
//	public void OnTickSleep(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
//	{
//		SeasonManager.GetOrCreate((ServerWorld) (Object) this).PassTime();
//	}

}
