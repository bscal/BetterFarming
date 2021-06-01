package me.bscal.betterfarming.common.components.chunk;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import me.bscal.betterfarming.BetterFarming;
import net.minecraft.util.Identifier;

public final class ChunkEcoProvider implements ChunkComponentInitializer
{

	public static final Identifier CHUNK_ECO_ID = new Identifier(BetterFarming.MOD_ID, "chunk_eco");
	public static final ComponentKey<IChunkEcoComponent> CHUNK_ECO = ComponentRegistry.getOrCreate(
			CHUNK_ECO_ID, IChunkEcoComponent.class);

	@Override
	public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry)
	{
		registry.register(CHUNK_ECO, ChunkEcoComponent::new);
	}
}
