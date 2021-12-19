package mirrg.gubot.noelle.screen;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.gubot.noelle.Helpers;
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.HLog;

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

	protected BufferedImage image;
	protected BufferedImage imageFace;
	protected BufferedImage imageSelecting;

	protected GUScreen(int screenX, int screenY, int width, int height)
	{
		this.screenX = screenX;
		this.screenY = screenY;
		this.width = width;
		this.height = height;
	}

	public abstract BufferedImage getImageBordered();

	public BufferedImage getImage()
	{
		if (image == null) image = getImageBordered().getSubimage(1, 1, width, height);
		return image;
	}

	public BufferedImage getImageFace()
	{
		if (imageFace == null) imageFace = getImage().getSubimage(15, 24, 64, 64);
		return imageFace;
	}

	public BufferedImage getImageSelecting()
	{
		if (imageSelecting == null) imageSelecting = getImage().getSubimage(584, 366, 184, 64);
		return imageSelecting;
	}

	public Rectangle getGameRegion()
	{
		return new Rectangle(screenX, screenY, width, height);
	}

	public void next()
	{
		mouseOn();
		GUNoelle.ROBOT.mousePress(InputEvent.getMaskForButton(MouseEvent.BUTTON1));

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {}

		mouseOn();
		GUNoelle.ROBOT.mouseRelease(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
	}

	public void mouseOn()
	{
		GUNoelle.ROBOT.mouseMove(screenX + width - 120, screenY + height - 115);
	}

	public Point getMouseLocation()
	{
		return MouseInfo.getPointerInfo().getLocation();
	}

	public boolean isSelecting()
	{
		return getDistanceSelecting() < 5;
	}

	public double getDistanceSelecting()
	{
		return Helpers.getDistance(IMAGE_SELECTING, getImageSelecting(), 5);
	}

	public boolean validate()
	{
		BufferedImage image = getImageBordered();

		if (!isAllBlack(image, 0, 0, width + 2, 1)) return false;
		if (!isAllBlack(image, 0, height + 1, width + 2, 1)) return false;
		if (!isAllBlack(image, 0, 0, 1, height + 2)) return false;
		if (!isAllBlack(image, width + 1, 0, 1, height + 2)) return false;

		return true;
	}

	private static boolean isAllBlack(BufferedImage image, int x, int y, int width, int height)
	{
		for (int x2 = x; x2 < x + width; x2 += 5) {
			for (int y2 = y; y2 < y + height; y2 += 5) {
				if ((image.getRGB(x2, y2) & 0xffffff) != 0) {
					return false;
				}
			}
		}
		return true;
	}

}
