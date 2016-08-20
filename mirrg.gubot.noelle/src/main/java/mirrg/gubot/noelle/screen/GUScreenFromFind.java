package mirrg.gubot.noelle.screen;

import java.awt.image.BufferedImage;

public class GUScreenFromFind extends GUScreen
{

	protected BufferedImage imageScreen;
	protected BufferedImage image;
	protected BufferedImage imageFace;
	protected BufferedImage imageSelecting;

	protected GUScreenFromFind(int screenX, int screenY, int width, int height, BufferedImage imageDisplay)
	{
		super(screenX, screenY, width, height);
		this.imageScreen = imageDisplay;
	}

	@Override
	public BufferedImage getImage()
	{
		if (image == null) image = imageScreen.getSubimage(screenX, screenY, width, height);
		return image;
	}

	@Override
	public BufferedImage getImageFace()
	{
		if (imageFace == null) imageFace = getImage().getSubimage(15, 24, 64, 64);
		return imageFace;
	}

	@Override
	public BufferedImage getImageSelecting()
	{
		if (imageSelecting == null) imageSelecting = getImage().getSubimage(584, 366, 184, 64);
		return imageSelecting;
	}

	@Override
	protected BufferedImage getScreenCapture(int x, int y, int width, int height)
	{
		return imageScreen.getSubimage(x, y, width, height);
	}

}
