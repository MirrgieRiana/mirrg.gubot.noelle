package mirrg.gubot.noelle.glyph;

import java.awt.image.BufferedImage;

import mirrg.gubot.noelle.Helpers;

public class Glyph
{

	public static final double DO_NOT_MATCH = 9999999;

	public String value;
	public String option;
	/**
	 * 真の文字幅は「実際の画像サイズ-2」。
	 * 上下も余白が1px入る。
	 */
	public BufferedImage image;
	public boolean isFixed;

	private Integer sumAlpha;

	public Glyph(String value, String option, BufferedImage image, boolean isFixed)
	{
		this.value = value;
		this.option = option;
		this.image = image;
		this.isFixed = isFixed;
	}

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

	public double getDistanceSq(BufferedImage challenger)
	{
		double t = 0;
		double limit = (isFixed ? 10 : 12000) * getSumAlpha() * 3;

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb1 = image.getRGB(x, y);
				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = (rgb1 >> 0) & 0xff;
				int rgb2 = challenger.getRGB(x, y);
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
