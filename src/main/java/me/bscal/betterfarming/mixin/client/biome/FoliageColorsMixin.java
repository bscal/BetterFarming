package me.bscal.betterfarming.mixin.client.biome;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.client.seasons.biome.BiomeChanger;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import me.bscal.betterfarming.common.seasons.SeasonSettings;
import me.bscal.betterfarming.common.seasons.Seasons;
import me.bscal.betterfarming.common.utils.Color;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoliageColors.class) public class FoliageColorsMixin
{

	private static final int FALL_BIRCH_COLOR = Color.fromHex("#e2b914").toInt();

	@Inject(method = "getBirchColor", at = @At(value = "RETURN"), cancellable = true)
	private static void OnGetBirchColor(CallbackInfoReturnable<Integer> cir)
	{
		BiomeSeasonHandler seasonHandler = BetterFarmingClient.GetBiomeSeasonHandler();
		if (seasonHandler.haveBiomeChangersLoaded)
		{
			BiomeChanger changer = seasonHandler.biomeEffectChangerMap.get(BiomeKeys.BIRCH_FOREST);
			if (changer != null)
			{
				if (SeasonSettings.Root.fallLeavesGraphics.getValue() != SeasonSettings.FallLeavesSettings.DISABLED && Seasons.GetSeason() == Seasons.AUTUMN)
					cir.setReturnValue(FALL_BIRCH_COLOR);
				else
					cir.setReturnValue(changer.GetColor(Seasons.GetSeason()));
			}
		}
	}

}
