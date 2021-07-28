package me.bscal.betterfarming.common.seasons;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;

public class SeasonSettings extends Config
{

	public static final ConfigItemGroup rootGroup = new Root();

	public static final List<ConfigItemGroup> configs = of(rootGroup);

	public SeasonSettings()
	{
		super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "seasons.json"), "seasons");
	}

	public static class Root extends ConfigItemGroup
	{

		public static final ConfigItem<Integer> ticksPerSeason = new ConfigItem<>("ticksPerSeason", 30 * 24000, "ticksPerSeason");

		public Root()
		{
			super(of(ticksPerSeason), "root");
		}
	}
}
