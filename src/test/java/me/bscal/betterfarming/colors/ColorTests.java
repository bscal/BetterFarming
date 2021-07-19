package me.bscal.betterfarming.colors;

import me.bscal.betterfarming.common.utils.Color;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorTests
{

	@Test
	public void TestColor()
	{
		Color color = new Color(174, 52, 52);
		System.out.println(color);
		assertEquals(color.toString(), "Color[r=174, g=52, b=52, a=255]");

		double[] hsl = color.toHsl();
		assertEquals(0, hsl[0]);
		assertEquals(54, Math.round(hsl[1] * 100));
		assertEquals(44, Math.round(hsl[2] * 100));
		Color hslColor = Color.fromHsl(hsl);
		System.out.println(hslColor);
		assertEquals(color, hslColor);

		double[] hsv = color.toHsv();
		assertEquals(0, hsv[0]);
		assertEquals(70, Math.round(hsv[1] * 100));
		assertEquals(68, Math.round(hsv[2] * 100));
		Color hsvColor = Color.fromHsv(hsv);
		System.out.println(hsvColor);
		assertEquals(color, hsvColor);

		String hex = color.toHex();
		System.out.println(hex);
		assertEquals("#ae3434", hex);
		Color hexColor = Color.fromHex(hex);
		assertEquals(color, hexColor);

		int cInt = color.toInt();
		System.out.println(cInt);
		assertEquals(-5360588, cInt);
		Color intColor = new Color(cInt);
		assertEquals(color, intColor);
	}


	@Test
	public void TestColorMethods()
	{
		// If this tests fails by a small amout it is prob from rounding/doubles -> ints
		Color color = new Color(174, 52, 52);
		assertEquals(0, color.getHue());
		assertEquals(44, color.getLightness());
		color.setHue(130);
		assertEquals(new Color(52, 174, 72), color);
		color.setLightness(88);
		assertEquals(new Color(208, 241, 213), color);
	}

	@Test
	public void TestColorSaturation()
	{
		// If this tests fails by a small amout it is prob from rounding/doubles -> ints
		Color c = new Color(44, 255, 79);
		assertEquals(83, c.getSaturation());
		c.setSaturation(30);
		assertEquals(new Color(179, 255, 191), c);
	}

}
