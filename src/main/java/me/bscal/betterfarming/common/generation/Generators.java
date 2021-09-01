package me.bscal.betterfarming.common.generation;

import me.bscal.betterfarming.BetterFarming;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.devtech.arrp.json.tags.JTag.*;
import static net.devtech.arrp.json.models.JModel.*;
import static net.devtech.arrp.api.RuntimeResourcePack.id;
import static net.devtech.arrp.json.loot.JLootTable.*;

public final class Generators
{
	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(BetterFarming.MOD_ID + ":assets");

	public static final ResourceGenerateable.Item DEFAULT = new ResourceGenerateable.Item() {};

	private Generators() {}

	public static Item register(ResourceGenerateable.Item generateable, String id, String en_us) {
		return register(generateable, new Item(new Item.Settings()), new Identifier(BetterFarming.MOD_ID, id), en_us);
	}

	public static <T extends Item> T register(ResourceGenerateable.Item generateable, T t, Identifier identifier, String en_us) {
		generateable.init(t);
		ResourceGen.registerClient(r -> generateable.client(r, identifier));
		ResourceGen.registerServer(r -> generateable.server(r, identifier));
		ResourceGen.registerLang("en_us", l -> l.item(identifier, en_us));
		return t;
	}

	public static void Init()
	{
		ResourceGen.init(RESOURCE_PACK);
		ResourceGen.registerLang("en_us", l -> l.entry(BetterFarming.MOD_ID + ".tooltip.smile", "The dust smiles at you"));
	}

}
