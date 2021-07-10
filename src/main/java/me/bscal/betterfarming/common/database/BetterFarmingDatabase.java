package me.bscal.betterfarming.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.bscal.betterfarming.BetterFarming;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public final class BetterFarmingDatabase
{

	public static final String TABLE_GROWABLE_BLOCKS = "growable_blocks";

	private static final HikariConfig CONFIG = new HikariConfig();
	private static final HikariDataSource DATA_SOURCE;

	static
	{
		CONFIG.setJdbcUrl("jdbc:h2:" + FabricLoader.getInstance().getConfigDir() + "\\data\\growable_blocks");
		DATA_SOURCE = new HikariDataSource(CONFIG);
	}

	private BetterFarmingDatabase() {}

	public static Connection GetConnection() throws SQLException
	{
		return DATA_SOURCE.getConnection();
	}

	public static void CreateTables()
	{
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_GROWABLE_BLOCKS +" ("
				+ "id                   integer   NOT NULL,"
				+ "world                varchar(64)   NOT NULL,"
				+ "x                    integer   NOT NULL,"
				+ "y                    integer   NOT NULL,"
				+ "z                    integer   NOT NULL,"
				+ "create_time          bigint   NOT NULL,"
				+ "block                varchar(64)   NOT NULL,"
				+ "CONSTRAINT pk_growable_blocks_id PRIMARY KEY ( id ));";
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

	public static void InsertBlock(World world, Block block, BlockPos pos, BlockState state)
	{
		String sql = String.format("INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?)", TABLE_GROWABLE_BLOCKS);
		try
		{
			Connection conn = DATA_SOURCE.getConnection();
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, world.getRegistryKey().getValue().toString());
			statement.setInt(2, pos.getX());
			statement.setInt(3, pos.getY());
			statement.setInt(4, pos.getZ());
			statement.setLong(5, Instant.now().getEpochSecond());
			statement.setString(6, Registry.BLOCK.getId(block).toString());
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
		String sql = "DELETE FROM blocks WHERE world = ? AND x = ? AND y = ? AND z = ?";
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

}
