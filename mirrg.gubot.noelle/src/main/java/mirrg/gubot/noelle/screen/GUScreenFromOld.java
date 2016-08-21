package mirrg.gubot.noelle.screen;

import java.awt.image.BufferedImage;

import mirrg.gubot.noelle.GUNoelle;

public class GUScreenFromOld extends GUScreen
{

	protected BufferedImage imageScreen;
	protected BufferedImage image;
	protected BufferedImage imageFace;
	protected BufferedImage imageSelecting;

	protected GUScreenFromOld(int screenX, int screenY, int width, int height)
	{
		super(screenX, screenY, width, height);
	}

	public BufferedImage getImageScreen()
	{
		if (imageScreen == null) imageScreen = GUNoelle.createScreenCapture(screenX - 1, screenY - 1, width + 2, height + 2);
		return imageScreen;
	}

	@Override
	public BufferedImage getImage()
	{
		if (image == null) image = getImageScreen().getSubimage(1, 1, width, height);
		return image;
	}

	@Override
	protected BufferedImage getScreenCapture(int x, int y, int width, int height)
	{
		return getImageScreen().getSubimage(x - (screenX - 1), y - (screenY - 1), width, height);
	}

}
