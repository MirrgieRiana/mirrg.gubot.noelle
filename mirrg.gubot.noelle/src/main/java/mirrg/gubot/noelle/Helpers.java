package mirrg.gubot.noelle;

import java.awt.image.BufferedImage;

public class Helpers
{

	public static double getDistance(BufferedImage overlay, BufferedImage challenger, int step)
	{
		double t = 0;
		int c = 0;

		for (int x = 0; x < overlay.getWidth(); x += step) {
			for (int y = 0; y < overlay.getHeight(); y += step) {
				int rgb1 = overlay.getRGB(x, y);
				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = (rgb1 >> 0) & 0xff;
				int rgb2 = challenger.getRGB(x, y);
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = (rgb2 >> 0) & 0xff;

				t += getWeightedDiff(r1, r2, a1) * a1;
				t += getWeightedDiff(g1, g2, a1) * a1;
				t += getWeightedDiff(b1, b2, a1) * a1;

				c += a1 * 3;
			}
		}

		return t / c;
	}

	/**
	 * <pre>
	 * alpha = 0.3
	 * |---------b--------a-----|
	 *      `-----------------'   safe area
	 *           `--------'       diff
	 *      `-------------'       lower safe length
	 * </pre>
	 *
	 * @param alpha
	 *            0~255
	 * @return Math.max(diff / (lower safe length) - 1, 0)
	 */
	private static double getWeightedDiff(double a, double b, int alpha)
	{
		if (a > b) {
			double diff = a - b;
			double lowerSafeLength = a * (255 - alpha) / 255;
			if (lowerSafeLength == 0) return diff * 5;
			return Math.max(diff / lowerSafeLength - 1, 0);
		} else {
			double diff = b - a;
			double upperSafeLength = (255 - a) * (255 - alpha) / 255;
			if (upperSafeLength == 0) return diff * 5;
			return Math.max(diff / upperSafeLength - 1, 0);
		}
	}

}
