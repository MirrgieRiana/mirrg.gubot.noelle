package mirrg.gubot.noelle.glyph;

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.stream.Stream;

import mirrg.gubot.noelle.Helpers;

public class GlyphSet
{

	public final String value;
	public final BufferedImage image;
	public final boolean isFixed;
	public final int div = 16;

	private Hashtable<EnumGlyphColor, Glyph[]> tableImage = new Hashtable<>();

	public GlyphSet(String value, BufferedImage image, boolean isFixed, int div, int distanceLimit)
	{
		this.value = value;
		this.image = image;
		this.isFixed = isFixed;

		tableImage.put(EnumGlyphColor.WHITE, getGlyph(value, EnumGlyphColor.WHITE, image, isFixed, div, distanceLimit));
		tableImage.put(EnumGlyphColor.RED, getGlyph(value, EnumGlyphColor.RED, image, isFixed, div, distanceLimit));
		tableImage.put(EnumGlyphColor.ORANGE, getGlyph(value, EnumGlyphColor.ORANGE, image, isFixed, div, distanceLimit));
		tableImage.put(EnumGlyphColor.PINK, getGlyph(value, EnumGlyphColor.PINK, image, isFixed, div, distanceLimit));

	}

	private static Glyph[] getGlyph(String value, EnumGlyphColor color, BufferedImage image, boolean isFixed, int div, int distanceLimit)
	{
		Glyph[] images = new Glyph[div];

		for (int i = 0; i < div; i++) {
			images[i] = new Glyph(
				value,
				color,
				i,
				Helpers.copyRightMoveSmall(Helpers.copy(image,
					color.color.getAlpha(),
					color.color.getRed(),
					color.color.getGreen(),
					color.color.getBlue()), 1.0 * i / div),
				isFixed,
				distanceLimit);
		}

		return images;
	}

	public Glyph get(EnumGlyphColor color, int subPixel)
	{
		return tableImage.get(color)[subPixel];
	}

	public Stream<Glyph> stream()
	{
		return tableImage.values().stream()
			.flatMap(t -> Stream.of(t));
	}

}
