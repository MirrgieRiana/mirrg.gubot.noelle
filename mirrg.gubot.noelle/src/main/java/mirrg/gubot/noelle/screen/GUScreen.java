package mirrg.gubot.noelle.screen;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.swing.neon.v1_1.artifacts.logging.HLog;

public abstract class GUScreen
{

	public static BufferedImage IMAGE_SELECTING;
	static {
		try {
			IMAGE_SELECTING = ImageIO.read(new File("imageSelecting.png"));
		} catch (IOException e) {
			HLog.processException(e);
			throw new RuntimeException(e);
		}
	}

	protected int screenX;
	protected int screenY;
	protected int width;
	protected int height;

	protected GUScreen(int screenX, int screenY, int width, int height)
	{
		this.screenX = screenX;
		this.screenY = screenY;
		this.width = width;
		this.height = height;
	}

	public abstract BufferedImage getImage();

	public abstract BufferedImage getImageFace();

	public abstract BufferedImage getImageSelecting();

	public void next()
	{
		GUNoelle.ROBOT.mouseMove(screenX + width - 35, screenY + height - 115);
		GUNoelle.ROBOT.mousePress(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {}
		GUNoelle.ROBOT.mouseRelease(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
	}

	public boolean isSelecting()
	{
		return getDistanceSelecting() < 5;
	}

	public double getDistanceSelecting()
	{
		BufferedImage imageSelecting = getImageSelecting();

		double t = 0;
		int c = 0;

		for (int x = 0; x < IMAGE_SELECTING.getWidth(); x += 5) {
			for (int y = 0; y < IMAGE_SELECTING.getHeight(); y += 5) {
				int rgb1 = IMAGE_SELECTING.getRGB(x, y);
				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = (rgb1 >> 0) & 0xff;
				int rgb2 = imageSelecting.getRGB(x, y);
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = (rgb2 >> 0) & 0xff;

				t += getWeightedDiff(r1, r2, a1);
				t += getWeightedDiff(g1, g2, a1);
				t += getWeightedDiff(b1, b2, a1);

				c++;
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
	protected static double getWeightedDiff(double a, double b, int alpha)
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

	protected abstract BufferedImage getScreenCapture(int x, int y, int width, int height);

	public boolean validate()
	{
		BufferedImage image;

		image = getScreenCapture(screenX - 1, screenY - 1, width + 2, 1);
		if (!isAllBlack(image)) return false;

		image = getScreenCapture(screenX - 1, screenY + height, width + 2, 1);
		if (!isAllBlack(image)) return false;

		image = getScreenCapture(screenX - 1, screenY - 1, 1, height + 2);
		if (!isAllBlack(image)) return false;

		image = getScreenCapture(screenX + width, screenY - 1, 1, height + 2);
		if (!isAllBlack(image)) return false;

		return true;
	}

	private static boolean isAllBlack(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if ((image.getRGB(x, y) & 0xffffff) != 0) {
					return false;
				}
			}
		}
		return true;
	}

}
