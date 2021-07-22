package me.bscal.betterfarming.mixin.common.entities;

import me.bscal.betterfarming.common.ai.goals.MoveToAndDrinkGoal;
import me.bscal.betterfarming.common.ai.goals.MoveToAndEatGoal;
import me.bscal.betterfarming.mixin.common.accessors.MobEntityAccessor;
import net.minecraft.entity.passive.CowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CowEntity.class) public class CowEntityMixin
{

	@Inject(method = "initGoals", at = @At(value = "TAIL"))
	public void OnInitGoals(CallbackInfo ci)
	{
		CowEntity cow = (CowEntity) (Object) this;
		MobEntityAccessor accessor = (MobEntityAccessor) cow;

		accessor.GetGoalSelector().add(4, new MoveToAndEatGoal(cow, 1.2, 16));
		accessor.GetGoalSelector().add(4, new MoveToAndDrinkGoal(cow, 1.2, 16, false));
	}

}
