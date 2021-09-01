package me.bscal.betterfarming.common.generation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.bscal.betterfarming.BetterFarming;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

@SuppressWarnings("VariableUseSideOnly") public class ResourceGen
{
	@Environment(EnvType.CLIENT) private static final Multimap<String, Consumer<JLang>> LANGUAGES = HashMultimap.create();
	private static Consumer<RuntimeResourcePack> server, client;

	public static void registerServer(Consumer<RuntimeResourcePack> consumer)
	{
		if (server == null)
		{
			server = consumer;
		}
		else
		{
			server = server.andThen(consumer);
		}
	}

	public static void registerClient(Consumer<RuntimeResourcePack> consumer)
	{
		if (BetterFarming.CLIENT)
		{
			if (client == null)
			{
				client = consumer;
			}
			else
			{
				client = client.andThen(consumer);
			}
		}
	}

	public static void registerLang(String language, Consumer<JLang> lang)
	{
		if (BetterFarming.CLIENT)
		{
			LANGUAGES.put(language, lang);
		}
	}

	public static Identifier prefixPath(Identifier identifier, String prefix)
	{
		return new Identifier(identifier.getNamespace(), prefix + '/' + identifier.getPath());
	}

	public static void init(RuntimeResourcePack pack)
	{
		if (server != null)
		{
			server.accept(pack);
		}
		if (BetterFarming.CLIENT)
		{
			if (client != null)
			{
				client.accept(pack);
			}
			LANGUAGES.asMap().forEach((s, c) -> {
				JLang lang = JLang.lang();
				for (Consumer<JLang> consumer : c)
				{
					consumer.accept(lang);
				}
				pack.addLang(new Identifier(BetterFarming.MOD_ID, s), lang);
			});
		}
	}
}
