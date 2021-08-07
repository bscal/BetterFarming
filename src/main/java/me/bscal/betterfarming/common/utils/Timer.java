package me.bscal.betterfarming.common.utils;

import org.apache.logging.log4j.Logger;

public class Timer
{

	private long m_start;
	private long m_duration;

	public void Start()
	{
		m_start = System.nanoTime();
	}

	public void Stop()
	{
		m_duration = System.nanoTime() - m_start;
	}

	public long GetDuration()
	{
		return m_duration;
	}

	public long GetDurationMillis()
	{
		return m_duration / 1000000;
	}

	public void Log(Logger log)
	{
		log.info(String.format("Duration: %dns, %fms", m_duration, m_duration * 0.000001));
	}

}
