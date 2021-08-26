package me.bscal.betterfarming.common.utils.schedulers;

import me.bscal.betterfarming.BetterFarming;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public abstract class SchedulableOwner
{

	public SchedulableOwner() {}

	public boolean IsValid() { return true; }

	public Object GetOwner() { return this; }

	public abstract void Serialize(NbtCompound nbt);

	public abstract void Deserialize(NbtCompound nbt);

	public static class BlockOwner extends SchedulableOwner
	{

		protected BlockPos pos;
		protected ServerWorld world;

		public BlockOwner()
		{
		}

		public BlockOwner(BlockPos pos, ServerWorld world)
		{
			this.pos = pos;
			this.world = world;
		}

		@Override
		public void Serialize(NbtCompound nbt)
		{
			nbt.putInt("x", pos.getX());
			nbt.putInt("y", pos.getY());
			nbt.putInt("z", pos.getZ());

			nbt.putString("world", world.getRegistryKey().getValue().toString());
		}

		@Override
		public void Deserialize(NbtCompound nbt)
		{
			pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
			world = BetterFarming.GetServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("world"))));
		}

	}

}
