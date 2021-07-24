package me.bscal.betterfarming.common.utils;

import java.util.Arrays;

public class Color
{
	public static Color BLACK = new Color(0, 0, 0);

	public int r, g, b, a;

	public Color(int r, int g, int b)
	{
		this(r, g, b, 255);
	}

	public Color(double r, double g, double b)
	{
		this.r = (int) Math.round(r);
		this.g = (int) Math.round(g);
		this.b = (int) Math.round(b);
		this.a = 255;
	}

	public Color(int r, int g, int b, int a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(int color)
	{
		this(color, false);
	}

	public Color(int color, boolean hasAlpha)
	{
		a = (hasAlpha) ? (color >> 24) & 0xff : 0;
		r = (color >> 16) & 0xff;
		g = (color >> 8) & 0xff;
		b = color & 0xff;
	}

	public void setRgbFromInt(int color)
	{
		a = (color >> 24) & 0xff;
		r = (color >> 16) & 0xff;
		g = (color >> 8) & 0xff;
		b = color & 0xff;
	}

	public void setRgbFromColor(Color color)
	{
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
	}

	public void blend(Color other)
	{
		double totalAlpha = a + other.a;
		double weight0 = a / totalAlpha;
		double weight1 = other.a / totalAlpha;

		double newR = weight0 * r + weight1 * other.a;
		double newG = weight0 * g + weight1 * other.r;
		double newB = weight0 * b + weight1 * other.b;
		double newA = Math.max(a, other.a);
		r = (int) Math.round(newR);
		g = (int) Math.round(newG);
		b = (int) Math.round(newB);
		a = (int) Math.round(newA);
	}

	public void setHue(int hue)
	{
		hue = Math.max(hue, 0);
		hue = Math.min(hue, 360);
		double[] hsv = toHsv(r, g, b);
		System.out.println(Arrays.toString(hsv));
		hsv[0] = hue / 360D;
		System.out.println(Arrays.toString(hsv));
		setRgbFromColor(fromHsv(hsv[0], hsv[1], hsv[2]));
	}

	public int getHue()
	{
		double[] hsv = toHsv(r, g, b);
		return (int) Math.round(hsv[0] * 360D);
	}

	public void saturate(int amount)
	{
		double[] hsv = toHsv(r, g, b);
		hsv[1] = Math.max(0D, Math.min(1D, hsv[1] + amount / 100D));
		setRgbFromColor(fromHsv(hsv[0], hsv[1], hsv[2]));
	}

	public void setSaturation(int saturation)
	{
		double[] hsv = toHsv(r, g, b);
		hsv[1] = saturation / 100D;
		setRgbFromColor(fromHsv(hsv[0], hsv[1], hsv[2]));
	}

	public int getSaturation()
	{
		double[] hsv = toHsv(r, g, b);
		return (int) Math.round(hsv[1] * 100);
	}

	public void lighten(int amount)
	{
		double[] hsl = toHsl(r, g, b);
		hsl[2] = hsl[2] + amount / 100D;
		setRgbFromColor(fromHsl(hsl[0], hsl[1], hsl[2]));
	}

	public void setLightness(int lightness)
	{
		double[] hsl = toHsl(r, g, b);
		hsl[2] = Math.max(0D, Math.min(1D, lightness / 100D));
		setRgbFromColor(fromHsl(hsl[0], hsl[1], hsl[2]));
	}

	public int getLightness()
	{
		double[] hsl = toHsl(r, g, b);
		return (int) Math.round(hsl[2] * 100);
	}

	public double[] toHsv()
	{
		return toHsv(r, g, b);
	}

	public double[] toHsv(double r, double g, double b)
	{
		r /= 255;
		g /= 255;
		b /= 255;

		double max = Math.max(r, Math.max(g, b));
		double min = Math.min(r, Math.min(g, b));
		double h, s, v = max;

		double d = max - min;
		s = max == 0 ? 0 : d / max;

		if (max == min)
		{
			h = 0; // achromatic
		}
		else
		{
			if (max == r)
				h = (g - b) / d + (g < b ? 6 : 0);
			else if (max == g)
				h = (b - r) / d + 2;
			else
				h = (r - g) / d + 4;

			h /= 6;
		}

		return new double[] { h, s, v };
	}

	public double[] toHsl()
	{
		return toHsl(r, g, b);
	}

	public double[] toHsl(double r, double g, double b)
	{
		r /= 255D;
		g /= 255D;
		b /= 255D;

		double max = Math.max(r, Math.max(b, g));
		double min = Math.min(r, Math.min(b, g));
		double h, s;
		double l = (max + min) / 2D;

		if (max == min)
			h = s = 0;
		else
		{
			double d = max - min;
			s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);

			if (max == r)
				h = (g - b) / d + (g < b ? 6 : 0);
			else if (max == g)
				h = (b - r) / d + 2;
			else
				h = (r - g) / d + 4;

			h /= 6;
		}

		return new double[] { h, s, l };
	}

	public String toHex()
	{
		//String alpha = pad(Integer.toHexString(a));
		String red = pad(Integer.toHexString(r));
		String green = pad(Integer.toHexString(g));
		String blue = pad(Integer.toHexString(b));
		return "#" + red + green + blue;
	}

	public int toInt()
	{
		return getIntFromColor(r, g, b, a);
	}

	public int toIntWithoutAlpha() { return getIntFromColor(r, g, b, false); }

	@Override
	public String toString()
	{
		return String.format("Color[r=%d, g=%d, b=%d, a=%d]", r, g, b, a);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Color && ((Color) obj).r == r && ((Color) obj).g == g && ((Color) obj).b == b && ((Color) obj).a == a;
	}

	public static int getIntFromColor(int r, int g, int b, boolean hasAlpha)
	{
		return getIntFromColor(r, g, b, (hasAlpha) ? 255 : 0);
	}

	public static int getIntFromColor(int r, int g, int b, int a)
	{
		a = (a << 24) & 0xff000000;
		r = (r << 16) & 0x00ff0000;
		g = (g << 8) & 0x0000ff00;
		b = b & 0x000000ff;
		return a | r | g | b;
	}

	public static int getIntFromColor(Color color)
	{
		return getIntFromColor(color.r, color.g, color.b, color.a);
	}

	public static Color fromHsl(double[] hsl)
	{
		return fromHsl(hsl[0], hsl[1], hsl[2]);
	}

	public static Color fromHsl(double h, double s, double l)
	{
		double r, g, b;

		if (s == 0)
		{
			r = g = b = (int) l; // achromatic
		}
		else
		{
			var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
			var p = 2 * l - q;

			r = hue2rgb(p, q, h + 1D / 3D);
			g = hue2rgb(p, q, h);
			b = hue2rgb(p, q, h - 1D / 3D);
		}
		return new Color(r * 255D, g * 255D, b * 255D);
	}

	public static Color fromHsv(double[] hsv)
	{
		return fromHsv(hsv[0], hsv[1], hsv[2]);
	}

	public static Color fromHsv(double h, double s, double v)
	{
		double r, g, b, f, p, q, t;
		r = g = b = 0;
		int i = (int) Math.floor(h * 6);
		f = h * 6 - i;
		p = v * (1 - s);
		q = v * (1 - f * s);
		t = v * (1 - (1 - f) * s);
		switch (i % 6)
		{
		case 0:
			r = v;
			g = t;
			b = p;
			break;
		case 1:
			r = q;
			g = v;
			b = p;
			break;
		case 2:
			r = p;
			g = v;
			b = t;
			break;
		case 3:
			r = p;
			g = q;
			b = v;
			break;
		case 4:
			r = t;
			g = p;
			b = v;
			break;
		case 5:
			r = v;
			g = p;
			b = q;
			break;
		}

		r = Math.round(r * 255);
		g = Math.round(g * 255);
		b = Math.round(b * 255);

		return new Color(r, g, b);
	}

	public static Color fromHex(String hex)
	{
		int red = Integer.parseInt(hex.substring(1, 3), 16);
		int green = Integer.parseInt(hex.substring(3, 5), 16);
		int blue = Integer.parseInt(hex.substring(5, 7), 16);
		return new Color(red, green, blue);
	}

	private static String pad(String s)
	{
		return (s.length() == 1) ? "0" + s : s;
	}

	private static double hue2rgb(double p, double q, double t)
	{
		if (t < 0D)
			t += 1D;
		if (t > 1D)
			t -= 1D;
		if (t < 1D / 6D)
			return p + (q - p) * 6D * t;
		if (t < 1D / 2D)
			return q;
		if (t < 2D / 3D)
			return p + (q - p) * (2D / 3D - t) * 6D;
		return p;
	}
}
