package me.bscal.betterfarming.common.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * An <code>Int2ObjectOpenHashMap</code> that uses the rawId from minecraft Registry's.
 * Contains various wrapper functions that help getting and putting from rawIds
 * @param <T> The type (T) of <code>Registry T</code> used for keys.
 * @param <V> The value of the <code>Object</code> in the map.
 */
public class RegistryMapToObject<T, V> extends Int2ObjectOpenHashMap<V>
{

	// Im pretty sure these are safe to cache.
	private final World m_world;
	private final Registry<T> m_registry;

	public RegistryMapToObject(final World world, final RegistryKey<Registry<T>> registryReference)
	{
		super();
		m_world = world;
		m_registry = world.getRegistryManager().get(registryReference);
	}

	public RegistryMapToObject(final World world, final RegistryKey<Registry<T>> registryReference, int expected)
	{
		super(expected);
		m_world = world;
		m_registry = world.getRegistryManager().get(registryReference);
	}

	public boolean containsFromRegistryType(T t)
	{
		return containsKey(m_registry.getRawId(t));
	}

	public boolean containsFromRegistryKey(RegistryKey<T> key)
	{
		return containsKey(m_registry.getRawId(m_registry.get(key)));
	}

	public boolean containsFromIdentifier(Identifier id)
	{
		return containsKey(m_registry.getRawId(m_registry.get(id)));
	}

	public V getFromRegistryType(T t)
	{
		return get(m_registry.getRawId(t));
	}

	public V getFromRegistryKey(RegistryKey<T> key)
	{
		return get(m_registry.getRawId(m_registry.get(key)));
	}

	public V getFromIdentifier(Identifier id)
	{
		return get(m_registry.getRawId(m_registry.get(id)));
	}

	public V putFromRegistryType(T t, V v)
	{
		return put(m_registry.getRawId(t), v);
	}

	public V putFromRegistryKey(RegistryKey<T> key, V v)
	{
		return put(m_registry.getRawId(m_registry.get(key)), v);
	}

	public V putFromIdentifier(Identifier id, V v)
	{
		return put(m_registry.getRawId(m_registry.get(id)), v);
	}

	public final World getWorld()
	{
		return m_world;
	}

	public final Registry<T> getRegistry()
	{
		return m_registry;
	}

}
