package me.bscal.betterfarming.common.database.blockdata;

import com.google.gson.*;
import me.bscal.betterfarming.common.utils.Utils;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Serializable BlockPos with a World Identifier
 */
public class WorldPos
{

	public @NotNull final Identifier worldId;
	public @NotNull final BlockPos pos;

	public WorldPos(@NotNull final Identifier worldId, @NotNull final BlockPos pos)
	{
		this.worldId = worldId;
		this.pos = pos;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		WorldPos worldPos = (WorldPos) o;
		return Objects.equals(worldId, worldPos.worldId) && Objects.equals(pos, worldPos.pos);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(worldId, pos);
	}

	@Override
	public String toString()
	{
		return worldId + "|" + pos.toShortString();
	}

	public static class Serializer implements JsonSerializer<WorldPos>, JsonDeserializer<WorldPos>
	{
		@Override
		public WorldPos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			String strJson = json.getAsString();
			String[] split = strJson.split("\\|");
			return new WorldPos(new Identifier(split[0]), new BlockPos(Utils.Vec3iFromShortString(split[1])));
		}

		@Override
		public JsonElement serialize(WorldPos src, Type typeOfSrc, JsonSerializationContext context)
		{
			return new JsonPrimitive(src.toString());
		}
	}
}
