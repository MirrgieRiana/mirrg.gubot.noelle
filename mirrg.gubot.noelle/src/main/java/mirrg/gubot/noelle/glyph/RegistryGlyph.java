package mirrg.gubot.noelle.glyph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import mirrg.gubot.noelle.RegistryHeroine;
import mirrg.swing.neon.v1_1.artifacts.logging.HLog;

public class RegistryGlyph
{

	public static final Pattern PATTERN_CSV_CELL = Pattern.compile(",?([^,]+)");

	private static Hashtable<String, GlyphSet> glyphSets = new Hashtable<>();
	static {
		File dir = new File("glyphs");
		Stream.of(dir.listFiles())
			.forEach(f -> {
				Matcher matcher = RegistryHeroine.PATTERN_FILENAME_BODY.matcher(f.getName());
				if (matcher.matches()) {

					ArrayList<String> cells = new ArrayList<>();
					{
						Matcher matcher2 = PATTERN_CSV_CELL.matcher(matcher.group(1));
						while (matcher2.find()) {
							cells.add(matcher2.group(1));
						}
					}

					BufferedImage image;
					try {
						image = ImageIO.read(f);
					} catch (IOException e) {
						HLog.processException(e);
						throw new RuntimeException(e);
					}

					glyphSets.put(cells.get(0), new GlyphSet(cells.get(0), image, cells.indexOf("fixed") >= 1));

				}
			});
	}

	public static Stream<GlyphSet> getGlyphSets()
	{
		return glyphSets.entrySet().stream()
			.map(e -> e.getValue());
	}

	public static GlyphSet getGlyphSet(String key)
	{
		return glyphSets.get(key);
	}

	public static Stream<Glyph> getGlyphs()
	{
		return getGlyphSets()
			.flatMap(s -> s.stream());
	}

}
