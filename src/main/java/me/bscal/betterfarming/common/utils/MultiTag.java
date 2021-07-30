package me.bscal.betterfarming.common.utils;

import net.minecraft.tag.Tag;

public class MultiTag<T>
{

	final Tag<T> included;
	final Tag<T> excluded;

	public MultiTag(Tag<T> included, Tag<T> excluded)
	{
		this.included = included;
		this.excluded = excluded;
	}

	public boolean isIncluded(T value)
	{
		return included.contains(value);
	}

	public boolean isExcluded(T value)
	{
		return excluded.contains(value);
	}

	public Tag<T> getIncluded()
	{
		return included;
	}

	public Tag<T> getExcluded()
	{
		return excluded;
	}

}
