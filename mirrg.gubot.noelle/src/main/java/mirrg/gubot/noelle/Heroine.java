package mirrg.gubot.noelle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import mirrg.swing.neon.v1_1.artifacts.logging.HLog;

public class Heroine
{

	public BufferedImage image;
	public String name;

	public Heroine(BufferedImage image, String name)
	{
		this.image = image;
		this.name = name;
	}

	public void save() throws IOException
	{
		File file = new File("faces/" + name + ".png");
		ImageIO.write(image, "png", file);
		HLog.fine("Saved as: " + file);
	}

	public double getDistance(BufferedImage image2, double limit)
	{
		if (image.getWidth() != image2.getWidth()) return 99999999;
		if (image.getHeight() != image2.getHeight()) return 99999999;
		int t = 0;
		int pixels = (image.getWidth() / 8) * (image.getHeight() / 8);

		limit *= pixels;

		for (int x = 0; x < image.getWidth(); x += 8) {
			for (int y = 0; y < image.getHeight(); y += 8) {
				int rgb = image.getRGB(x, y);
				int rgb2 = image2.getRGB(x, y);

				t += Math.abs(((rgb >> 16) & 0xff) - ((rgb2 >> 16) & 0xff));
				t += Math.abs(((rgb >> 8) & 0xff) - ((rgb2 >> 8) & 0xff));
				t += Math.abs((rgb & 0xff) - (rgb2 & 0xff));

				if (t > limit) return 1.0 * t / pixels;
			}
		}

		return 1.0 * t / pixels;
	}

	public String getButtleClass()
	{
		return RegistryHeroine.buttleClasses.get(name);
	}

}
