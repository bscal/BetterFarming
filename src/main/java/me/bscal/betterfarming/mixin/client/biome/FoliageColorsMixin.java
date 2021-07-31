package me.bscal.betterfarming.mixin.client.biome;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.client.seasons.biome.BiomeChanger;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import me.bscal.betterfarming.common.seasons.Seasons;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoliageColors.class) public class FoliageColorsMixin
{

	@Inject(method = "getBirchColor", at = @At(value = "RETURN"), cancellable = true)
	private static void OnGetBirchColor(CallbackInfoReturnable<Integer> cir)
	{
		BiomeSeasonHandler seasonHandler = BetterFarmingClient.GetBiomeSeasonHandler();
		if (seasonHandler.haveBiomeChangersLoaded)
		{
			BiomeChanger changer = seasonHandler.biomeEffectChangerMap.get(BiomeKeys.BIRCH_FOREST);
			if (changer != null)
				cir.setReturnValue(changer.GetColor(Seasons.GetSeason()));
		}
	}

}
