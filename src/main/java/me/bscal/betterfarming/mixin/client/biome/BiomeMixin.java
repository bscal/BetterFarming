package me.bscal.betterfarming.mixin.client.biome;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.client.seasons.biome.BiomeChanger;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
import me.bscal.betterfarming.common.seasons.Seasons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class) public class BiomeMixin
{

	@Inject(method = "Lnet/minecraft/world/biome/Biome;getGrassColorAt(DD)I", at = @At(value = "HEAD"), cancellable = true)
	public void OnGetGrassColorAt(double x, double z, CallbackInfoReturnable<Integer> cir)
	{
		Biome biome = (Biome) (Object) this;
		BiomeSeasonHandler seasonHandler = BetterFarmingClient.GetBiomeSeasonHandler();
		if (seasonHandler.haveBiomeChangersLoaded)
		{
			MinecraftClient.getInstance().world.getRegistryManager()
					.get(Registry.BIOME_KEY)
					.getKey(biome)
					.ifPresent((key) -> {
						BiomeChanger changer = seasonHandler.biomeEffectChangerMap.get(key);
						if (changer != null)
							cir.setReturnValue(changer.GetColor(Seasons.GetSeason()));
					});

		}
	}

	@Inject(method = "getFoliageColor", at = @At(value = "HEAD"), cancellable = true)
	public void OnGetFoliageColor(CallbackInfoReturnable<Integer> cir)
	{
		Biome biome = (Biome) (Object) this;
		BiomeSeasonHandler seasonHandler = BetterFarmingClient.GetBiomeSeasonHandler();
		if (seasonHandler.haveBiomeChangersLoaded)
		{
			MinecraftClient.getInstance().world.getRegistryManager()
					.get(Registry.BIOME_KEY)
					.getKey(biome)
					.ifPresent((key) -> {
						BiomeChanger changer = seasonHandler.biomeEffectChangerMap.get(key);
						if (changer != null)
							cir.setReturnValue(changer.GetFoliageColor(Seasons.GetSeason()));
					});
		}
	}

	//@Inject(method = "getTemperature()F", at = @At(value = "RETURN"))
	//public void OnGetTemperature(CallbackInfoReturnable<Float> cir)
	//{
	//
	//}

}
