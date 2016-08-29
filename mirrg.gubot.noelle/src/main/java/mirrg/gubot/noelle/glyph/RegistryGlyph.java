package mirrg.gubot.noelle.glyph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import mirrg.gubot.noelle.Helpers;
import mirrg.gubot.noelle.RegistryHeroine;
import mirrg.swing.neon.v1_1.artifacts.logging.HLog;

public class RegistryGlyph
{

	public static final Pattern PATTERN_CSV_CELL = Pattern.compile(",?([^,]+)");

	private static ArrayList<Glyph> glyphs = new ArrayList<>();
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

					BufferedImage image1;
					try {
						image1 = ImageIO.read(f);
					} catch (IOException e) {
						HLog.processException(e);
						throw new RuntimeException(e);
					}

					boolean isFixed = cells.contains("fixed");

					double div = 16;
					for (int i = 0; i < div; i++) {

						// 白
						glyphs.add(new Glyph(cells.get(0), "W[" + i + "]", Helpers.copyRightMoveSmall(image1, i / div), isFixed));

						// 赤
						glyphs.add(new Glyph(cells.get(0), "R[" + i + "]", Helpers.copyRightMoveSmall(Helpers.copy(image1, 255, 255, 0, 0), i / div), isFixed));

						// 橙
						glyphs.add(new Glyph(cells.get(0), "O[" + i + "]", Helpers.copyRightMoveSmall(Helpers.copy(image1, 255, 255, 127, 0), i / div), isFixed));

						// 桃
						glyphs.add(new Glyph(cells.get(0), "P[" + i + "]", Helpers.copyRightMoveSmall(Helpers.copy(image1, 255, 255, 127, 127), i / div), isFixed));

					}

				}
			});
	}

	public static Stream<Glyph> getGlyphs()
	{
		return glyphs.stream();
	}

}
