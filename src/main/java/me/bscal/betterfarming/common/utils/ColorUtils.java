package me.bscal.betterfarming.common.utils;

public class ColorUtils
{

	public static int GetIntFromColor(int r, int g, int b)
	{
		return GetIntFromColor(r, g, b, 0xff000000);
	}

	public static int GetIntFromColor(int r, int g, int b, int a)
	{
		a = (a << 24) & 0xff000000;
		r = (r << 16) & 0x00ff0000;
		g = (g << 8) & 0x0000ff00;
		b = b & 0x000000ff;
		return a | r | g | b;
	}

	public static class Color
	{
		public static Color BLACK = new Color(0, 0, 0);

		int r, g, b, a;

		public Color(int r, int g, int b)
		{
			this(r, g, b, 0xff000000);
		}

		public Color(int r, int g, int b, int a)
		{
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

		public static Color fromHSL(double h, double s, double l)
		{
			int r, g, b;

			if (s == 0)
			{
				r = g = b = (int) l; // achromatic
			}
			else
			{
				var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
				var p = 2 * l - q;

				r = (int) hue2rgb(p, q, h + 1D / 3D);
				g = (int) hue2rgb(p, q, h);
				b = (int) hue2rgb(p, q, h - 1D / 3D);
			}
			return new Color(r * 255, g * 255, b * 255);
		}

		public double[] rgbToHsv(int r, int g, int b)
		{
			double max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
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

		public double[] rgbToHsl(int r, int g, int b)
		{
			double h, s, l;
			double max, min;
			max = Math.max(r, Math.max(b, g));
			min = Math.min(r, Math.min(b, g));
			h = s = l = (max + min) / 2;
			if (max == min)
				h = s = 0;
			else
			{
				double d = max - min;
				s = (int) (l > 0.5 ? d / (2.0 - max - min) : d / (max + min));

				if (max == r)
					h = (g - b) / d + (g < b ? 6 : 0);
				else if (max == g)
					h = (b - r) / d + 2;
				else
					h = (r - g) / d + 4;
			}
			h /= 6;
			return new double[] { h, s, l };
		}

		public Color fromHSV(double h, double s, double v)
		{
			double r, g, b;

			var i = Math.floor(h * 6);
			var f = h * 6 - i;
			var p = v * (1 - s);
			var q = v * (1 - f * s);
			var t = v * (1 - (1 - f) * s);

			switch ((int) (i % 6))
			{
			case 0 -> {
				r = v;
				g = t;
				b = p;
			}
			case 1 -> {
				r = q;
				g = v;
				b = p;
			}
			case 2 -> {
				r = p;
				g = v;
				b = t;
			}
			case 3 -> {
				r = p;
				g = q;
				b = v;
			}
			case 4 -> {
				r = t;
				g = p;
				b = v;
			}
			case 5 -> {
				r = v;
				g = p;
				b = q;
			}
			default -> throw new IllegalStateException("Unexpected value: " + (int) (i % 6));
			}

			return new Color((int) (r * 255), (int) (g * 255), (int) (b * 255));
		}

		public static double hue2rgb(double p, double q, double t)
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
}
