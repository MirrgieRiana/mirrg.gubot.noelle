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

	public int getDistance(BufferedImage image2)
	{
		if (image.getWidth() != image2.getWidth()) return 99999999;
		if (image.getHeight() != image2.getHeight()) return 99999999;
		int t = 0;

		for (int x = 0; x < image.getWidth(); x += 8) {
			for (int y = 0; y < image.getHeight(); y += 8) {
				int rgb = image.getRGB(x, y);
				int rgb2 = image2.getRGB(x, y);

				t += Math.abs(((rgb & 0xff0000) >> 16) - ((rgb2 & 0xff0000) >> 16));
				t += Math.abs(((rgb & 0xff00) >> 8) - ((rgb2 & 0xff00) >> 8));
				t += Math.abs((rgb & 0xff) - (rgb2 & 0xff));

			}
		}

		return t;
	}

	public String getButtleClass()
	{
		return RegistryHeroine.buttleClasses.get(name);
	}

}
