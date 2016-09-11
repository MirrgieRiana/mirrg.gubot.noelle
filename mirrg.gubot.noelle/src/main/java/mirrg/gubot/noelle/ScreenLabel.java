package mirrg.gubot.noelle;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;

public class ScreenLabel extends JLabel
{

	/**
	 *
	 */
	private static final long serialVersionUID = 2415051591221706988L;

	public ScreenLabel(int width, int height)
	{
		setMaximumSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));

		setBackground(new Color(32, 32, 32));
		setOpaque(true);
	}

}
