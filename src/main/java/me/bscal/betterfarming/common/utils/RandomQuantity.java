package me.bscal.betterfarming.common.utils;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class RandomQuantity
{

	private static final Random RAND = new Random();

	public static int Rand(int min, int max)
	{
		return RAND.nextInt(max + 1) + min;
	}

	public static int Bonus(int bonus, float chance)
	{
		return RAND.nextFloat() < chance ? bonus : 0;
	}

	public static int Clamp(int value, int min, int max)
	{
		return Math.min(max, Math.max(min, value));
	}

	public static int Pick(int... values)
	{
		return values[RAND.nextInt(values.length)];
	}

	public static int Increase(int value, float increase)
	{
		return (int) (value + value * increase);
	}

	public static int IncreaseRound(int value, float increase)
	{
		return Math.round(value + value * increase);
	}

	/**
	 * Increases value by increase (percentage). value will increase by a minimum of +/-1 unless increase is 0 which will return value
	 */
	public static int IncreaseMin(int value, float increase)
	{
		int newValue = (int) (value + value * increase);
		return (newValue != value) ? newValue : (increase > 0f) ? value + 1 : (increase < 0f) ? value - 1 : value;
	}

	public static int Apply(int original, Supplier<Integer> supplier)
	{
		return original + supplier.get();
	}

	public static int Test(int original, Predicate<Void> predicate)
	{
		return predicate.test(null) ? original + 1 : original;
	}

	public static <T> T Probability(Supplier<T> positive, Supplier<T> negative, float probability)
	{
		return RAND.nextFloat() <= probability ? positive.get() : negative.get();
	}

	public static int Binomial(int n, float p)
	{
		int j = 0;
		for (int k = 0; k < n; ++k)
		{
			if (RAND.nextFloat() < p)
			{
				++j;
			}
		}
		return j;
	}

	public static int Probability(ProbabilityPair... pairs)
	{
		int sum = 0;
		for (var pair : pairs)
		{
			sum += pair.probability;
		}
		int target = RAND.nextInt(sum);
		int total = 0;
		for (var pair : pairs)
		{
			total += pair.probability;
			if (target < total)
			{
				return pair.value;
			}
		}
		return 0;
	}

	public static record ProbabilityPair(int value, float probability)
	{
	}

}
