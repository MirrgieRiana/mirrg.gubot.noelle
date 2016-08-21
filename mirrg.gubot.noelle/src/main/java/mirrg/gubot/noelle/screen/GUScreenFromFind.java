package mirrg.gubot.noelle.screen;

import java.awt.image.BufferedImage;

public class GUScreenFromFind extends GUScreen
{

	protected BufferedImage imageScreen;
	private BufferedImage imageBordered;

	protected GUScreenFromFind(int screenX, int screenY, int width, int height, BufferedImage imageScreen)
	{
		super(screenX, screenY, width, height);
		this.imageScreen = imageScreen;
	}

	@Override
	public BufferedImage getImageBordered()
	{
		if (imageBordered == null) imageBordered = imageScreen.getSubimage(screenX - 1, screenY - 1, width + 2, height + 2);
		return imageBordered;
	}

}
