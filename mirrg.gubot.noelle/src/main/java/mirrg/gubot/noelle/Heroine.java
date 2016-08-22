package mirrg.gubot.noelle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import mirrg.swing.neon.v1_1.artifacts.logging.HLog;

public class Heroine
{

	public BufferedImage image;
	public BufferedImage imageToMatch;
	public String name;

	public Heroine(BufferedImage image, String name)
	{
		this.image = image;
		this.imageToMatch = getImageToMatch(image);
		this.name = name;
	}

	public static BufferedImage getImageToMatch(BufferedImage image)
	{
		return Helpers.copyNormalize(Helpers.copyStep(image, 2));
	}

	public void save() throws IOException
	{
		File file = new File("faces/" + name + ".png");
		ImageIO.write(image, "png", file);
		HLog.fine("Saved as: " + file);
	}

	public double getDistance(BufferedImage imageToMatch2, double limit)
	{
		if (imageToMatch.getWidth() != imageToMatch2.getWidth()) return 99999999;
		if (imageToMatch.getHeight() != imageToMatch2.getHeight()) return 99999999;
		int t = 0;
		int pixels = imageToMatch.getWidth() * imageToMatch.getHeight();

		limit *= pixels;

		for (int x = 0; x < imageToMatch.getWidth(); x++) {
			for (int y = 0; y < imageToMatch.getHeight(); y++) {
				int rgb = imageToMatch.getRGB(x, y);
				int rgb2 = imageToMatch2.getRGB(x, y);

				t += Helpers.power2(Math.abs(((rgb >> 16) & 0xff) - ((rgb2 >> 16) & 0xff)));
				t += Helpers.power2(Math.abs(((rgb >> 8) & 0xff) - ((rgb2 >> 8) & 0xff)));
				t += Helpers.power2(Math.abs((rgb & 0xff) - (rgb2 & 0xff)));

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
