package me.bscal.betterfarming.common.mixin.biome;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.client.seasons.biome.BiomeChangers;
import me.bscal.betterfarming.common.utils.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
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
		if (biome.equals(MinecraftClient.getInstance().world.getRegistryManager().get(Registry.BIOME_KEY).get(
				BiomeChangers.PLAINS_CHANGER.key)))
		{
			cir.setReturnValue(new Color(BetterFarming.RAND.nextInt(255), BetterFarming.RAND.nextInt(255), 0, 255).toInt());
		}

	}

}
