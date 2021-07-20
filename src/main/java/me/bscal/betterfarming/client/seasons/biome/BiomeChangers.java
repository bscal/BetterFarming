package me.bscal.betterfarming.client.seasons.biome;

import me.bscal.betterfarming.common.mixin.biome.BiomeInvoker;
import me.bscal.betterfarming.common.utils.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.BiomeKeys;

@Environment(EnvType.CLIENT)
public class BiomeChangers
{

	public static final BiomeEffectHandler.BiomeEffectChanger PLAINS_CHANGER;

	static
	{
		PLAINS_CHANGER = new PlainsChanger();
	}

	public static class PlainsChanger extends BiomeEffectHandler.BiomeEffectChanger
	{
		public PlainsChanger()
		{
			super(BiomeKeys.PLAINS);
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

