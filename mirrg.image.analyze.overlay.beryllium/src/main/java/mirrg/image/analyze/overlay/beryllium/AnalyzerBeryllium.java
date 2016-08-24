package mirrg.image.analyze.overlay.beryllium;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Stream;

public class AnalyzerBeryllium
{

	public static BufferedImage analyze(ImagePair... pairs)
	{
		if (pairs.length < 2) throw new IllegalArgumentException("too less pairs: " + pairs.length + " < 2");
		int width = pairs[0].width;
		int height = pairs[0].height;
		BufferedImage image5 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double[] argb = calculateARGB(pairs, x, y);
				image5.setRGB(x, y,
					(trimColor(argb[0]) << 24) |
						(trimColor(argb[1]) << 16) |
						(trimColor(argb[2]) << 8) |
						(trimColor(argb[3]) << 0));
			}
		}

		return image5;
	}

	private static double[] calculateARGB(ImagePair[] pairs, int x, int y)
	{
		double[] argb = new double[4];

		// Alpha算出
		{
			ArrayList<double[]> alphas = new ArrayList<>();
			for (int i = 0; i < pairs.length - 1; i++) {
				for (int j = i + 1; j < pairs.length; j++) {
					pairs[i].getAlphaAndWeight(alphas, pairs[j], x, y);
				}
			}

			double[] count = new double[1];
			alphas.stream()
				.forEach(a -> {
					argb[0] += a[0] * a[1];
					count[0] += a[1];
				});
			argb[0] /= count[0];
		}

		// Color算出
		{
			double[] count = new double[1];
			Stream.of(pairs)
				.map(p -> p.getRGB(x, y, argb[0]))
				.forEach(c -> {
					argb[1] += c[0];
					argb[2] += c[1];
					argb[3] += c[2];
					count[0]++;
				});
			argb[1] /= count[0];
			argb[2] /= count[0];
			argb[3] /= count[0];
		}

		argb[0] *= 255;

		return argb;
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

}
