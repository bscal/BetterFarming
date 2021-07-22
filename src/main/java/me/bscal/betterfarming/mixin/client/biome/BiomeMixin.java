package me.bscal.betterfarming.mixin.client.biome;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.client.seasons.biome.BiomeChanger;
import me.bscal.betterfarming.client.seasons.biome.BiomeSeasonHandler;
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
			BiomeChanger changer = seasonHandler.biomeEffectChangerMap.get(biome);
			if (changer != null)
				cir.setReturnValue(changer.GetColor(BetterFarmingClient.GetSeason()));
		}

	}

}
