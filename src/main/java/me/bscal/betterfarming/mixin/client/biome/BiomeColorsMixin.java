package me.bscal.betterfarming.mixin.client.biome;

import me.bscal.betterfarming.client.BetterFarmingClient;
import me.bscal.betterfarming.common.seasons.SeasonSettings;
import me.bscal.betterfarming.common.seasons.Seasons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin
{

	@Mutable @Shadow @Final public static ColorResolver FOLIAGE_COLOR;

	@Mutable
	@Accessor("FOLIAGE_COLOR")
	static ColorResolver getFoliageColor()
	{
		return null;
	}

	@Mutable
	@Accessor("FOLIAGE_COLOR")
	static void setFoliageColor(ColorResolver resolver) {}

	static
	{
		FOLIAGE_COLOR = BiomeColorsMixin::FoliageColorOverride;
	}

	private static int FoliageColorOverride(Biome biome, double x, double y)
	{
		if (SeasonSettings.Root.fallLeavesGraphics.getValue() != SeasonSettings.FallLeavesSettings.DISABLED && BetterFarmingClient.GetBiomeSeasonHandler().seasonClock.currentSeason == Seasons.AUTUMN)
		{
			var optional = MinecraftClient.getInstance().player.world.getRegistryManager().get(Registry.BIOME_KEY).getKey(biome);
			if (optional.isPresent())
			{
				return BetterFarmingClient.GetBiomeSeasonHandler().biomeEffectChangerMap.get(optional.get()).GetRandomFallColor((int)x, (int)y);
			}
		}
		return biome.getFoliageColor();
	}
}
