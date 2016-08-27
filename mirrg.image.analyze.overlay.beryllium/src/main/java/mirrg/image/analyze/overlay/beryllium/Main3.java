package mirrg.image.analyze.overlay.beryllium;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main3
{

	/**
	 * 黒背景と白背景の画像からオーバーレイ画像（グレースケール固定）を算出する。
	 */
	public static void main(String[] args) throws IOException
	{
		File dir = new File("workspace");
		BufferedImage srcWhite = ImageIO.read(new File(dir, "white.png"));
		BufferedImage srcBlack = ImageIO.read(new File(dir, "black.png"));
		BufferedImage dest = new BufferedImage(srcWhite.getWidth(), srcWhite.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < srcWhite.getWidth(); x++) {
			for (int y = 0; y < srcWhite.getHeight(); y++) {
				int rgbW = srcWhite.getRGB(x, y);
				double rW = ((rgbW >> 16) & 0xff) / 255.0;
				double gW = ((rgbW >> 8) & 0xff) / 255.0;
				double bW = ((rgbW >> 0) & 0xff) / 255.0;
				int rgbB = srcBlack.getRGB(x, y);
				double rB = ((rgbB >> 16) & 0xff) / 255.0;
				double gB = ((rgbB >> 8) & 0xff) / 255.0;
				double bB = ((rgbB >> 0) & 0xff) / 255.0;

				double a = 0;
				{
					double c = 0;

					a += (rB - rW + 1) * Math.abs(rB - rW + 1);
					c += Math.abs(rB - rW + 1);
					a += (gB - gW + 1) * Math.abs(gB - gW + 1);
					c += Math.abs(gB - gW + 1);
					a += (bB - bW + 1) * Math.abs(bB - bW + 1);
					c += Math.abs(bB - bW + 1);

					a /= c;
				}

				double r = rB / a;
				double g = gB / a;
				double b = bB / a;

				int rgb = (AnalyzerBeryllium.trimColor(a * 255) << 24) |
					(AnalyzerBeryllium.trimColor(r * 255) << 16) |
					(AnalyzerBeryllium.trimColor(g * 255) << 8) |
					(AnalyzerBeryllium.trimColor(b * 255) << 0);
				dest.setRGB(x, y, rgb);
			}
		}

		ImageIO.write(dest, "png", new File(dir, "out3.png"));
	}

}
