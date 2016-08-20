package mirrg.gubot.noelle.screen;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import mirrg.gubot.noelle.GUNoelle;

public class GUScreenFromOld extends GUScreen
{

	protected BufferedImage image;
	protected BufferedImage imageFace;
	protected BufferedImage imageSelecting;

	protected GUScreenFromOld(int screenX, int screenY, int width, int height)
	{
		super(screenX, screenY, width, height);
	}

	@Override
	public BufferedImage getImage()
	{
		if (image == null) {
			image = GUNoelle.ROBOT.createScreenCapture(new Rectangle(screenX, screenY, width, height));
		}
		return image;
	}

	@Override
	public BufferedImage getImageFace()
	{
		if (imageFace == null) {
			if (image != null) {
				imageFace = image.getSubimage(15, 24, 64, 64);
			} else {
				imageFace = GUNoelle.ROBOT.createScreenCapture(new Rectangle(screenX + 15, screenY + 24, 64, 64));
			}
		}
		return imageFace;
	}

	@Override
	public BufferedImage getImageSelecting()
	{
		if (imageSelecting == null) {
			if (image != null) {
				imageSelecting = image.getSubimage(584, 366, 184, 64);
			} else {
				imageSelecting = GUNoelle.ROBOT.createScreenCapture(new Rectangle(screenX + 584, screenY + 366, 184, 64));
			}
		}
		return imageSelecting;
	}

	@Override
	protected BufferedImage getScreenCapture(int x, int y, int width, int height)
	{
		return GUNoelle.ROBOT.createScreenCapture(new Rectangle(x, y, width, height));
	}

}
