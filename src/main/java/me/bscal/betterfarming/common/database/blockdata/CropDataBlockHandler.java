package me.bscal.betterfarming.common.database.blockdata;

import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.database.blockdata.blocks.CropDataBlock;
import me.bscal.betterfarming.common.database.blockdata.worldpos.WorldPosDataManager;
import me.bscal.betterfarming.common.seasons.SeasonalCrop;
import me.bscal.betterfarming.common.seasons.Seasons;
import me.bscal.betterfarming.common.utils.Utils;
import me.bscal.betterfarming.common.utils.schedulers.FastIntervalScheduler;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class CropDataBlockHandler
{

	private static WorldPosDataManager WORLD_DATA_MANAGER;

	private CropDataBlockHandler()
	{
	}

	public static void Init(MinecraftServer server)
	{
		WORLD_DATA_MANAGER = new WorldPosDataManager(server, BetterFarming.MOD_ID + "_cropdata", CropDataBlock::new);

		FastIntervalScheduler.INSTANCE.RegisterRunnable(300, (entry) -> CropDataBlockHandler.UpdateUnloadedEntries(
				300 / Utils.GeometricDistributionMeanForRandomTicks(BetterFarming.TICK_SPEED)));

	}

	public static WorldPosDataManager GetManager()
	{
		return WORLD_DATA_MANAGER;
	}

	public static boolean IsInitialized()
	{
		return WORLD_DATA_MANAGER != null;
	}

	public static void UpdateUnloadedEntries(float estimatedRandomTicksPassed)
	{
		ServerWorld world = WORLD_DATA_MANAGER.GetWorld().GetServerWorld();
		for (var pair : WORLD_DATA_MANAGER.GetWorld().GetPairs())
		{
			ChunkPos pos = new ChunkPos(pair.key());
			boolean chunkLoaded = world.isChunkLoaded(pos.x, pos.z);
			// Estimated time that if BlockData has not been updated since, we can presume that the chunk is unloaded and does not update.
			if (!chunkLoaded)
			{
				for (var entry : pair.value())
					// We can presume that when the block was unloaded and the last tick was a success that it can continue to tick while
					// unloaded. It is incremented with estimated # of randomTicks and should not cause unloaded chunks to load.
					UpdateGrowable(BlockPos.fromLong(entry.getLongKey()), (CropDataBlock) entry.getValue(), world,
							estimatedRandomTicksPassed);
			}
		}
	}

	private static void UpdateGrowable(BlockPos pos, CropDataBlock data, World world, float estimatedRandomTicksPassed)
	{
		if (data.ableToGrow)
		{
			SeasonalCrop crop = BetterFarming.CROP_MANAGER.Get(data.block);
			if (crop == null)
			{
				if (data.GetBlock() instanceof SaplingBlock)
					TickSapling(data);
			}
			Biome biome = world.getBiome(pos);
			int season = Seasons.GetSeasonForBiome(biome, BetterFarming.SEASON_CLOCK.currentSeason);

			boolean isRandomTickLessThan1 = estimatedRandomTicksPassed < 1f && BetterFarming.RAND.nextFloat() < estimatedRandomTicksPassed;
			float multiplier = ((isRandomTickLessThan1) ? estimatedRandomTicksPassed : Math.max(estimatedRandomTicksPassed, 1));
			crop.ApplyGrowth(data, season, 0, multiplier);
		}
		BetterFarming.LOGGER.info("Updated unloaded BlockData");
	}

	public static void Save()
	{
		WORLD_DATA_MANAGER.Save();
	}

	public static void TickSapling(CropDataBlock cropDataBlock)
	{
		float growth = 1f + cropDataBlock.growthModifier;
		cropDataBlock.totalGrowthReceived += growth;
		cropDataBlock.currentAgeGrowthReceived += growth;
		if (cropDataBlock.totalGrowthReceived > 24000 * 10 / SeasonalCrop.RANDOM_TICK_DEFAULT_AVERAGE)
		{
			cropDataBlock.age++;
			cropDataBlock.currentAgeGrowthReceived = 0;
		}
	}
}
