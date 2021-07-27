package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.common.utils.Color;
import me.bscal.betterfarming.mixin.client.biome.BiomeInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

@Environment(EnvType.CLIENT) public final class BiomeChangers
{

	public static Color WINTER_DEAD_BLUE = Color.fromHex("#b3b3b3");
	public static Color AUTUMN_DEEP_YELLOW = Color.fromHex("#ffcc00");

	public static class SimpleBiomeChanger extends BiomeChanger
	{
		public SimpleBiomeChanger(RegistryKey<Biome> key, ClientWorld world)
		{
			super(key, world);
			grassColors = new int[4];
			foliageColor = new int[4];

			int defaultColor = biome.getEffects()
					.getGrassColor()
					.orElse(((BiomeInvoker) (Object) biome).invokeGetDefaultGrassColor());

			Color spring = new Color(defaultColor);
			spring.saturate(40);
			grassColors[0] = spring.toInt();
			foliageColor[0] = spring.toInt();

			grassColors[1] = defaultColor;
			foliageColor[1] = defaultColor;

			Color fall = new Color(defaultColor);
			fall.blend(AUTUMN_DEEP_YELLOW, .15f);
			grassColors[2] = fall.toInt();
			foliageColor[2] = fall.toInt();

			Color winter = new Color(defaultColor);
			winter.blend(WINTER_DEAD_BLUE);
			grassColors[3] = winter.toInt();
			foliageColor[3] = winter.toInt();
		}
	}

	public static class PlainsChanger extends BiomeChanger
	{
		public PlainsChanger(ClientWorld world)
		{
			super(BiomeKeys.PLAINS, world);
			grassColors = new int[4];

			int tmpInt = biome.getEffects()
					.getGrassColor()
					.orElse(((BiomeInvoker) (Object) biome).invokeGetDefaultGrassColor());
			Color spring = new Color(tmpInt, false);
			spring.setLightness(80);
			spring.saturate(20);
			grassColors[0] = spring.toInt();
			grassColors[1] = tmpInt;
			Color fall = new Color(tmpInt, false);
			fall.saturate(-20);
			grassColors[2] = fall.toInt();
			Color winter = new Color(tmpInt, false);
			winter.saturate(-20);
			winter.lighten(-20);
			grassColors[3] = winter.toInt();
		}
	}

}

