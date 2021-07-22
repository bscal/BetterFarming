package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.mixin.client.biome.BiomeInvoker;
import me.bscal.betterfarming.common.utils.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.biome.BiomeKeys;

@Environment(EnvType.CLIENT)
public final class BiomeChangers
{

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

