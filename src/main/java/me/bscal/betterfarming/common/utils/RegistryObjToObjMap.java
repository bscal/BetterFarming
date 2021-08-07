package me.bscal.betterfarming.common.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.Optional;

public class RegistryObjToObjMap<T, V> extends Object2ObjectOpenHashMap<T, V>
{
	private final Registry<T> m_registry;

	public RegistryObjToObjMap(Registry<T> registryRef)
	{
		this.m_registry = registryRef;
	}

	public boolean containsFromRegistryKey(RegistryKey<T> key)
	{
		return m_registry.contains(key);
	}

	public boolean containsFromIdentifier(Identifier id)
	{
		return m_registry.containsId(id);
	}

	public V getFromRegistryKey(RegistryKey<T> key)
	{
		return get(m_registry.get(key));
	}

	public V getFromIdentifier(Identifier id)
	{
		return get(m_registry.get(id));
	}

	public V putFromRegistryKey(RegistryKey<T> key, V v)
	{
		return put(m_registry.get(key), v);
	}

	public V putFromIdentifier(Identifier id, V v)
	{
		return put(m_registry.get(id), v);
	}

	public Optional<RegistryKey<T>> getRegistryKey(T t)
	{
		return m_registry.getKey(t);
	}

	public Identifier getId(T t)
	{
		return m_registry.getId(t);
	}

	public final Registry<T> getRegistry()
	{
		return m_registry;
	}

}
