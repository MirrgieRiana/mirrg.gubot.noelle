package mirrg.gubot.noelle.screen;

import java.awt.image.BufferedImage;

import mirrg.gubot.noelle.GUNoelle;

public class GUScreenFromOld extends GUScreen
{

	protected BufferedImage imageBordered;

	protected GUScreenFromOld(int screenX, int screenY, int width, int height)
	{
		super(screenX, screenY, width, height);
	}

	@Override
	public BufferedImage getImageBordered()
	{
		if (imageBordered == null) imageBordered = GUNoelle.createScreenCapture(screenX - 1, screenY - 1, width + 2, height + 2);
		return imageBordered;
	}

}
