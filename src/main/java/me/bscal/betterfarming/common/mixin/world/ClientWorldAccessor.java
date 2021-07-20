package me.bscal.betterfarming.common.mixin.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientWorld.class)
public interface ClientWorldAccessor
{

	@Accessor("colorCache")
	Object2ObjectArrayMap<ColorResolver, BiomeColorCache> getColorCache();

}
