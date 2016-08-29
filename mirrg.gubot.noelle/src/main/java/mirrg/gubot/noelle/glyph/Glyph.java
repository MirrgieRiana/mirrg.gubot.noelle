package mirrg.gubot.noelle.glyph;

import java.awt.image.BufferedImage;

import mirrg.gubot.noelle.Helpers;

public class Glyph
{

	public static final double DO_NOT_MATCH = 9999999;

	public final String value;
	public final EnumGlyphColor color;
	public final int subPixelIndex;
	public final BufferedImage image;
	public final boolean isFixed;

	public Glyph(String value, EnumGlyphColor color, int subPixelIndex, BufferedImage image, boolean isFixed)
	{
		this.value = value;
		this.color = color;
		this.subPixelIndex = subPixelIndex;
		this.image = image;
		this.isFixed = isFixed;
	}

	public int getWidth()
	{
		return image.getWidth();
	}

	public int getTrueWidth()
	{
		return getWidth() - 2;
	}

	private Integer sumAlpha;

	public int getSumAlpha()
	{
		if (sumAlpha == null) {
			sumAlpha = 0;
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					sumAlpha += (image.getRGB(x, y) >> 24) & 0xff;
				}
			}
		}
		return sumAlpha;
	}

	public static double getDistanceLimit(boolean isFixed)
	{
		return isFixed ? 10 : 12000;
	}

	public double getDistanceSq(BufferedImage image1, boolean isFixed)
	{
		double t = 0;
		double limit = getDistanceLimit(isFixed) * getSumAlpha() * 3;

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb1 = image.getRGB(x, y);
				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = (rgb1 >> 0) & 0xff;
				int rgb2 = image1.getRGB(x, y);
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = (rgb2 >> 0) & 0xff;

				double diff;
				diff = Helpers.getWeightedDiff(r1, r2, a1);
				t += diff * diff * a1;
				diff = Helpers.getWeightedDiff(g1, g2, a1);
				t += diff * diff * a1;
				diff = Helpers.getWeightedDiff(b1, b2, a1);
				t += diff * diff * a1;

				if (t > limit) return DO_NOT_MATCH;
			}
		}

		return t / getSumAlpha() / 3;
	}

}
