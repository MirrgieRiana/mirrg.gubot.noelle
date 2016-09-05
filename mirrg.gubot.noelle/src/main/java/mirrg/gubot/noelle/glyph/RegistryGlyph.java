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
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.HLog;

public class RegistryGlyph
{

	public static final Pattern PATTERN_CSV_CELL = Pattern.compile(",?([^,]+)");

	public static RegistryGlyph normal = new RegistryGlyph(new File("glyphs/normal"), 16, 12000);
	public static RegistryGlyph small = new RegistryGlyph(new File("glyphs/small"), 32, 60000);

	private Hashtable<String, GlyphSet> glyphSets = new Hashtable<>();

	public int div;
	public int distanceLimit;

	public RegistryGlyph(File dir, int div, int distanceLimit)
	{
		this.div = div;
		this.distanceLimit = distanceLimit;
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

					glyphSets.put(cells.get(0), new GlyphSet(cells.get(0), image, cells.indexOf("fixed") >= 1, div, distanceLimit));

				}
			});
	}

	public Stream<GlyphSet> getGlyphSets()
	{
		return glyphSets.entrySet().stream()
			.map(e -> e.getValue());
	}

	public GlyphSet getGlyphSet(String key)
	{
		return glyphSets.get(key);
	}

	public Stream<Glyph> getGlyphs()
	{
		return getGlyphSets()
			.flatMap(s -> s.stream());
	}

}
