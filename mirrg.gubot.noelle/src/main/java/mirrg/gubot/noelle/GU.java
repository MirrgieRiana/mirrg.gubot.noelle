package mirrg.gubot.noelle;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import mirrg.swing.neon.v1_1.artifacts.logging.HLog;

public class GU
{

	public int screenX;
	public int screenY;

	public BufferedImage image;
	public BufferedImage imageFace;

	public GU(int screenX, int screenY, BufferedImage image)
	{
		this.screenX = screenX;
		this.screenY = screenY;
		this.image = image;
		this.imageFace = image.getSubimage(15, 24, 64, 64);
	}

	public void next()
	{
		try {
			Robot robot = new Robot();
			robot.mouseMove(screenX + image.getWidth() - 35, screenY + image.getHeight() - 115);
			robot.mousePress(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			robot.mouseRelease(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
		} catch (AWTException e) {
			HLog.processException(e);
		}
	}

}
