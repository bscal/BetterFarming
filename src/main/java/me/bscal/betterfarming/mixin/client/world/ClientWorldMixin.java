package me.bscal.betterfarming.mixin.client.world;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.common.seasons.SeasonSettings;
import me.bscal.betterfarming.common.seasons.Seasons;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Not implemented. Attempted at having 3d colors for foliage... works but.. not really how i want.
 */
@Mixin(ClientWorld.class) public class ClientWorldMixin
{

//	@Inject(method = "getColor", at = @At(value = "HEAD"), cancellable = true)
//	public void OnGetColor(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir)
//	{
//		if (SeasonSettings.Root.enableFallLeaves.getValue() && colorResolver.equals(
//				BiomeColors.FOLIAGE_COLOR) && BetterFarmingClient.GetBiomeSeasonHandler().seasonClock.currentSeason == Seasons.AUTUMN)
//		{
//			ClientWorld world = (ClientWorld) (Object) this;
//			var optional = world.getRegistryManager().get(Registry.BIOME_KEY).getKey(world.getBiome(pos));
//			optional.ifPresent(biomeRegistryKey -> cir.setReturnValue(
//					BetterFarmingClient.GetBiomeSeasonHandler().biomeEffectChangerMap.get(biomeRegistryKey).GetRandomFallColor()));
//		}
//	}

}
