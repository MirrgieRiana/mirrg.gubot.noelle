package mirrg.gubot.noelle.screen;

import java.awt.image.BufferedImage;

public class GUScreenFromFind extends GUScreen
{

	protected BufferedImage imageScreen;
	protected BufferedImage image;

	protected GUScreenFromFind(int screenX, int screenY, int width, int height, BufferedImage imageScreen)
	{
		super(screenX, screenY, width, height);
		this.imageScreen = imageScreen;
	}

	@Override
	public BufferedImage getImage()
	{
		if (image == null) image = imageScreen.getSubimage(screenX, screenY, width, height);
		return image;
	}

	@Override
	protected BufferedImage getScreenCapture(int x, int y, int width, int height)
	{
		return imageScreen.getSubimage(x, y, width, height);
	}

}
