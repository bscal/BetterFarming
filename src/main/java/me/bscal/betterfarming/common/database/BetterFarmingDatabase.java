/*
package me.bscal.betterfarming.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.bscal.betterfarming.BetterFarming;
import me.bscal.betterfarming.common.seasons.SeasonManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class BetterFarmingDatabase
{

	public static final String TABLE_GROWABLE_BLOCKS = "growable_blocks";

	private static final HikariConfig CONFIG = new HikariConfig();
	private static final HikariDataSource DATA_SOURCE;

	static
	{
		CONFIG.setJdbcUrl("jdbc:h2:" + FabricLoader.getInstance().getConfigDir() + "\\data" + "\\growable_blocks");
		DATA_SOURCE = new HikariDataSource(CONFIG);
	}

	private BetterFarmingDatabase()
	{
	}

	public static Connection GetConnection() throws SQLException
	{
		return DATA_SOURCE.getConnection();
	}

	public static void CreateTables()
	{
		String sql =
				"CREATE TABLE IF NOT EXISTS " + TABLE_GROWABLE_BLOCKS + " (" + "id integer NOT NULL, " + "worldId varchar(64) NOT " +
						"NULL, " + "x integer NOT NULL, " + "y integer NOT NULL, " + "z integer NOT NULL, " + "start_ticks bigint NOT " +
						"NULL, " + "current_growth_ticks integer NOT NULL, " + "current_age integer NOT NULL, " + "blockId varchar(64) NOT" +
						" NULL, " + "CONSTRAINT " + "pk_growable_blocks_id + PRIMARY KEY ( id ));";
		try
		{
			Connection conn = DATA_SOURCE.getConnection();
			conn.createStatement().execute(sql);
			conn.close();
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}
	}

	public static Optional<BlockQueryData> GetBlockByPos(World world, BlockPos pos)
	{
		String sql = String.format(
				"SELECT start_ticks, current_growth_ticks, current_age, blockId FROM %s WHERE worldId = ?, x = ?, y = ?, z = ?",
				TABLE_GROWABLE_BLOCKS);
		try
		{
			Connection conn = DATA_SOURCE.getConnection();
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, world.getRegistryKey().getValue().toString());
			statement.setInt(2, pos.getX());
			statement.setInt(3, pos.getY());
			statement.setInt(4, pos.getZ());
			ResultSet set = statement.executeQuery();
			if (set.first())    // There should really only ever be 1 world,x,y,z in the database
			{
				return Optional.of(new BlockQueryData(world, pos, set.getLong(1), set.getInt(2), set.getInt(3),
						Identifier.tryParse(set.getString(4))));
			}
			conn.close();
			if (BetterFarming.DEBUG)
				BetterFarming.LOGGER.info("UPDATED block into database");
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}
		return Optional.empty();
	}

	public static void UpdateBlock(World world, Block block, BlockPos pos, BlockState state, int currentAgesTick, int currentAge)
	{
		String sql = "UPDATE " + TABLE_GROWABLE_BLOCKS + " SET current_growth_ticks = ?, current_age = ? WHERE worldId = ? AND x = ? AND y" +
				" " + "= ? AND z = ?";
		try
		{
			Connection conn = DATA_SOURCE.getConnection();
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, currentAgesTick);
			statement.setInt(2, currentAge);
			statement.setString(3, world.getRegistryKey().getValue().toString());
			statement.setInt(4, pos.getX());
			statement.setInt(5, pos.getY());
			statement.setInt(6, pos.getZ());
			statement.execute();
			conn.close();

			if (BetterFarming.DEBUG)
				BetterFarming.LOGGER.info("UPDATED block into database");
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}
	}

	public static void InsertBlock(World world, Block block, BlockPos pos, BlockState state)
	{
		String sql = "INSERT INTO " + TABLE_GROWABLE_BLOCKS + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try
		{
			Connection conn = DATA_SOURCE.getConnection();
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, world.getRegistryKey().getValue().toString());
			statement.setInt(2, pos.getX());
			statement.setInt(3, pos.getY());
			statement.setInt(4, pos.getZ());
			statement.setLong(5, SeasonManager.GetOrCreate().GetSeasonClock().ticksSinceCreation);
			statement.setInt(6, 0);
			statement.setInt(7, 0);
			statement.setString(8, Registry.BLOCK.getId(block).toString());
			statement.execute();
			conn.close();

			if (BetterFarming.DEBUG)
				BetterFarming.LOGGER.info("INSERTED block into database");
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}
	}

	public static void DeleteBlock(World world, Block block, BlockPos pos, BlockState state)
	{
		String sql = "DELETE FROM " + TABLE_GROWABLE_BLOCKS + " WHERE worldId = ? AND x = ? AND y = ? AND z = ?";
		try
		{
			Connection conn = DATA_SOURCE.getConnection();
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, world.getRegistryKey().getValue().toString());
			statement.setInt(2, pos.getX());
			statement.setInt(3, pos.getY());
			statement.setInt(4, pos.getZ());
			statement.execute();
			conn.close();

			if (BetterFarming.DEBUG)
				BetterFarming.LOGGER.info("DELETED block into database");
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}
	}

	public static class BlockQueryData
	{
		public World world;
		public BlockPos pos;
		public Identifier blockId;
		public Block block;
		public long entryTicks;
		public int currentTicks;
		public int currentAge;

		public BlockQueryData(World world, BlockPos pos, long entryTicks, int currentTicks, int currentAge, Identifier blockId)
		{
			this.world = world;
			this.pos = pos;
			this.blockId = blockId;
			this.block = Registry.BLOCK.get(blockId);
			this.entryTicks = entryTicks;
			this.currentTicks = currentTicks;
			this.currentAge = currentAge;
		}
	}

}
*/
