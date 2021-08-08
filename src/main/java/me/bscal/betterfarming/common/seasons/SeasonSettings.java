package me.bscal.betterfarming.common.seasons;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;
import me.bscal.betterfarming.BetterFarming;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;

public class SeasonSettings extends Config
{

	public enum FallLeavesSettings
	{
		DISABLED,
		FAST,
		FANCY
	}

	public static final ConfigItemGroup rootGroup = new Root();

	public static final List<ConfigItemGroup> configs = of(rootGroup);

	public SeasonSettings()
	{
		super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "seasons.json"), "seasons");
	}

	public static class Root extends ConfigItemGroup
	{

		public static final ConfigItemGroup generationGroup = new Generation();

		public static final ConfigItem<Integer> ticksPerSeason = new ConfigItem<>("ticksPerSeason", 30 * 24000, "ticksPerSeason");
		public static final ConfigItem<FallLeavesSettings> fallLeavesGraphics = new ConfigItem<>("fallLeavesGraphics", FallLeavesSettings.FANCY, "fallLeavesGraphics");

		public Root()
		{
			super(of(generationGroup, ticksPerSeason, fallLeavesGraphics), "root");
		}

		public static class Generation extends ConfigItemGroup
		{

			public static final ConfigItem<Integer> seasonSeed = new ConfigItem<>("seasonalSeed",
					BetterFarming.RAND.nextInt(Integer.MAX_VALUE), "seasonalSeed");

			public Generation()
			{
				super(of(seasonSeed), "generation");
			}
		}
	}
}
