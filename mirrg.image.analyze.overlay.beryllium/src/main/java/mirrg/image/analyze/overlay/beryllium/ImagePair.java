package mirrg.image.analyze.overlay.beryllium;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * <pre>
 * r1 = back: 0~255
 * r2 = overlayed: 0~255
 * a: 0~1
 * r: 0~255
 *
 * (1 - a) * r1 + a * r = r2
 * r1 - a * r1 + a * r = r2
 * -a * r1 + a * r = r2 - r1
 * -r1 + r = (r2 - r1) / a
 * <b>r = (r2 - r1) / a + r1</b>
 *
 * { r = (r2 - r1) / a + r1
 * { r = (r4 - r3) / a + r3
 * (r2 - r1) / a + r1 = (r4 - r3) / a + r3
 * (r2 - r1) / a - (r4 - r3) / a = r3 - r1
 * ((r2 - r1) - (r4 - r3)) / a = r3 - r1
 * <b>a = ((r2 - r1) - (r4 - r3)) / (r3 - r1)</b>
 * </pre>
 */
public class ImagePair
{

	public final BufferedImage imageBack;
	public final BufferedImage imageOverlayed;

	public final int width;
	public final int height;

	public ImagePair(BufferedImage imageBack, BufferedImage imageOverlayed)
	{
		this.imageBack = imageBack;
		this.imageOverlayed = imageOverlayed;
		if (imageBack.getWidth() != imageOverlayed.getWidth()) {
			throw new IllegalArgumentException("different width: " + imageBack.getWidth() + "!=" + imageOverlayed.getWidth());
		}
		if (imageBack.getHeight() != imageOverlayed.getHeight()) {
			throw new IllegalArgumentException("different height: " + imageBack.getHeight() + "!=" + imageOverlayed.getHeight());
		}
		width = imageBack.getWidth();
		height = imageBack.getHeight();
	}

	/**
	 * @return {0~1, any}
	 */
	public void getAlphaAndWeight(ArrayList<double[]> dest, ImagePair other, int x, int y)
	{
		double r1 = (imageBack.getRGB(x, y) >> 16) & 0xff;
		double r2 = (imageOverlayed.getRGB(x, y) >> 16) & 0xff;
		double r3 = (other.imageBack.getRGB(x, y) >> 16) & 0xff;
		double r4 = (other.imageOverlayed.getRGB(x, y) >> 16) & 0xff;
		double g1 = (imageBack.getRGB(x, y) >> 8) & 0xff;
		double g2 = (imageOverlayed.getRGB(x, y) >> 8) & 0xff;
		double g3 = (other.imageBack.getRGB(x, y) >> 8) & 0xff;
		double g4 = (other.imageOverlayed.getRGB(x, y) >> 8) & 0xff;
		double b1 = (imageBack.getRGB(x, y) >> 0) & 0xff;
		double b2 = (imageOverlayed.getRGB(x, y) >> 0) & 0xff;
		double b3 = (other.imageBack.getRGB(x, y) >> 0) & 0xff;
		double b4 = (other.imageOverlayed.getRGB(x, y) >> 0) & 0xff;

		if (r3 - r1 != 0) {
			dest.add(new double[] {
				(r2 + r3 - r1 - r4) / (r3 - r1),
				Math.abs(r3 - r1),
			});
		}
		if (g3 - g1 != 0) {
			dest.add(new double[] {
				(g2 + g3 - g1 - g4) / (g3 - g1),
				Math.abs(g3 - g1),
			});
		}
		if (b3 - b1 != 0) {
			dest.add(new double[] {
				(b2 + b3 - b1 - b4) / (b3 - b1),
				Math.abs(b3 - b1),
			});
		}
	}

	/**
	 * @param alpha
	 *            0~1
	 */
	public double[] getRGB(int x, int y, double alpha)
	{
		double r1 = (imageBack.getRGB(x, y) >> 16) & 0xff;
		double r2 = (imageOverlayed.getRGB(x, y) >> 16) & 0xff;
		double g1 = (imageBack.getRGB(x, y) >> 8) & 0xff;
		double g2 = (imageOverlayed.getRGB(x, y) >> 8) & 0xff;
		double b1 = (imageBack.getRGB(x, y) >> 0) & 0xff;
		double b2 = (imageOverlayed.getRGB(x, y) >> 0) & 0xff;

		if (alpha == 0) {
			return new double[] {
				0,
				0,
				0,
			};
		} else {
			return new double[] {
				(r2 - r1) / alpha + r1,
				(g2 - g1) / alpha + g1,
				(b2 - b1) / alpha + b1,
			};
		}
	}

}
