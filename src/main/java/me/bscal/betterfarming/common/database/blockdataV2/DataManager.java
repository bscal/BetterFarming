package me.bscal.betterfarming.common.database.blockdataV2;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DataManager
{

	private final List<DataWorld> m_DataWorld;

	public DataManager(MinecraftServer server)
	{
		this.m_DataWorld = new ArrayList<>(5);
		this.m_DataWorld.add(new DataWorld(server.getOverworld(), null));
		this.m_DataWorld.add(new DataWorld(server.getWorld(World.NETHER), null));
		this.m_DataWorld.add(new DataWorld(server.getWorld(World.END), null));
	}

	public void RegisterWorld(@NotNull ServerWorld world, @Nullable Supplier<IDataBlock> factory)
	{
		m_DataWorld.add(new DataWorld(world, factory));
	}

	public void Save()
	{
		m_DataWorld.forEach(DataWorld::Serialize);
	}

	public void Load()
	{
		m_DataWorld.forEach(DataWorld::Deserialize);
	}

}
