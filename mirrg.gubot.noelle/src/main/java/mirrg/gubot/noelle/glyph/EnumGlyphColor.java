package mirrg.gubot.noelle.glyph;

import java.awt.Color;

public enum EnumGlyphColor
{
	WHITE(new Color(255, 255, 255)),
	RED(new Color(255, 0, 0)),
	ORANGE(new Color(255, 127, 0)),
	PINK(new Color(255, 127, 127)),
	;

	public final Color color;

	private EnumGlyphColor(Color color)
	{
		this.color = color;
	}

}
