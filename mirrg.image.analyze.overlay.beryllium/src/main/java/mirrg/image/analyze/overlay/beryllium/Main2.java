package mirrg.image.analyze.overlay.beryllium;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main2
{

	private static double r1 = 255 / 255.0;
	private static double g1 = 0 / 255.0;

	/**
	 * 一様な背景色を持つ画像からオーバーレイ画像（グレースケール固定）を算出する。
	 */
	public static void main(String[] args) throws IOException
	{
		File dir = new File("workspace");
		BufferedImage src = ImageIO.read(new File(dir, "002.png"));
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				int rgb = src.getRGB(x, y);
				double a = ((rgb >> 24) & 0xff) / 255.0;
				double r = ((rgb >> 16) & 0xff) / 255.0;
				double g = ((rgb >> 8) & 0xff) / 255.0;
				double b = ((rgb >> 0) & 0xff) / 255.0;

				{
					double c = (r * g1 - g * r1) / (r - r1 + g1 - g);
					a = Math.abs(c - r1) > Math.abs(c - g1) ? (r - r1) / (c - r1) : (g - g1) / (c - g1);
					r = g = b = c;
				}

				rgb = (AnalyzerBeryllium.trimColor(a * 255) << 24) |
					(AnalyzerBeryllium.trimColor(r * 255) << 16) |
					(AnalyzerBeryllium.trimColor(g * 255) << 8) |
					(AnalyzerBeryllium.trimColor(b * 255) << 0);
				dest.setRGB(x, y, rgb);
			}
		}

		ImageIO.write(dest, "png", new File(dir, "out2.png"));
	}

}
