package me.bscal.betterfarming.common.database.blockdata;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.bscal.betterfarming.common.utils.LongPair;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IBlockDataChunk
{

	NbtCompound ToNbt(NbtCompound nbt);

	IBlockDataChunk FromNbt(NbtCompound nbt);

	IBlockDataBlock GetBlock(BlockPos pos);

	IBlockDataBlock GetOrCreate(BlockPos pos, Supplier<IBlockDataBlock> blockDataFactory);

	void PutBlock(BlockPos pos, IBlockDataBlock blockData);

	IBlockDataBlock RemoveBlock(BlockPos pos);

	IBlockDataBlock[] GetAll(ServerWorld world);

	Object GetMap();

	void ForEach(Consumer<IBlockDataBlock> foreach);

	int Size();

}
