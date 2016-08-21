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

	public static int trimColor(double value)
	{
		return trim((int) value, 0, 255);
	}

	public static int trim(int value, int min, int max)
	{
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	public static double trim(double value, double min, double max)
	{
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	public static BufferedImage copy(BufferedImage src, int a, int r, int g, int b)
	{
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				int argb = src.getRGB(x, y);
				int a2 = (argb >> 24) & 0xff;
				int r2 = (argb >> 16) & 0xff;
				int g2 = (argb >> 8) & 0xff;
				int b2 = (argb >> 0) & 0xff;

				a2 = a2 * a / 255;
				r2 = r2 * r / 255;
				g2 = g2 * g / 255;
				b2 = b2 * b / 255;

				dest.setRGB(x, y, (a2 << 24) | (r2 << 16) | (g2 << 8) | b2);
			}
		}
		return dest;
	}

	public static BufferedImage copyGray(BufferedImage src)
	{
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				int argb = src.getRGB(x, y);
				int a = (argb >> 24) & 0xff;
				int r = (argb >> 16) & 0xff;
				int g = (argb >> 8) & 0xff;
				int b = (argb >> 0) & 0xff;

				r = (r + g + b) / 3;
				g = r;
				b = r;

				dest.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
			}
		}
		return dest;
	}

	public static BufferedImage copyStep(BufferedImage src, int step)
	{
		BufferedImage dest = new BufferedImage((src.getWidth() - 1) / step + 1, (src.getHeight() - 1) / step + 1, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < dest.getWidth(); x++) {
			for (int y = 0; y < dest.getHeight(); y++) {
				dest.setRGB(x, y, src.getRGB(x * step, y * step));
			}
		}
		return dest;
	}

	public static BufferedImage copyNormalize(BufferedImage src)
	{
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int rMin = 255;
		int gMin = 255;
		int bMin = 255;
		int rMax = 0;
		int gMax = 0;
		int bMax = 0;
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				int argb = src.getRGB(x, y);
				int r = (argb >> 16) & 0xff;
				int g = (argb >> 8) & 0xff;
				int b = (argb >> 0) & 0xff;

				if (r > rMax) rMax = r;
				if (g > gMax) gMax = g;
				if (b > bMax) bMax = b;
				if (r < rMin) rMin = r;
				if (g < gMin) gMin = g;
				if (b < bMin) bMin = b;
			}
		}
		int rRange = rMax - rMin;
		int gRange = gMax - gMin;
		int bRange = bMax - bMin;
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				int argb = src.getRGB(x, y);
				int a = (argb >> 24) & 0xff;
				int r = (argb >> 16) & 0xff;
				int g = (argb >> 8) & 0xff;
				int b = (argb >> 0) & 0xff;

				r = rRange != 0 ? (r - rMin) * 255 / rRange : 0;
				g = gRange != 0 ? (g - gMin) * 255 / gRange : 0;
				b = bRange != 0 ? (b - bMin) * 255 / bRange : 0;

				dest.setRGB(x, y, (trim(a, 0, 255) << 24) | (trim(r, 0, 255) << 16) | (trim(g, 0, 255) << 8) | trim(b, 0, 255));
			}
		}
		return dest;
	}

}
