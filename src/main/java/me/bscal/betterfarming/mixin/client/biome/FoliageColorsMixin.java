package me.bscal.betterfarming.mixin.client.biome;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.client.seasons.biome.BiomeChanger;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoliageColors.class)
public class FoliageColorsMixin
{

	@Inject(method = "getBirchColor", at = @At(value = "RETURN"), cancellable = true)
	private static void OnGetBirchColor(CallbackInfoReturnable<Integer> cir)
	{
		BiomeSeasonHandler seasonHandler = BetterFarmingClient.GetBiomeSeasonHandler();
		if (seasonHandler.haveBiomeChangersLoaded)
		{
			Biome biome = MinecraftClient.getInstance().world.getRegistryManager().get(Registry.BIOME_KEY).get(BiomeKeys.BIRCH_FOREST);
			BiomeChanger changer = seasonHandler.biomeEffectChangerMap.get(biome);
			if (changer != null)
				cir.setReturnValue(changer.GetColor(BetterFarmingClient.GetSeason()));
		}
	}

}
