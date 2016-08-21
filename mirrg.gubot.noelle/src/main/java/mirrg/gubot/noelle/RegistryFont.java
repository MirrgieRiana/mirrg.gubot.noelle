package mirrg.gubot.noelle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import mirrg.struct.hydrogen.v1_0.Tuple;
import mirrg.swing.neon.v1_1.artifacts.logging.HLog;

public class RegistryFont
{

	private static ArrayList<Tuple<String, BufferedImage>> fonts = new ArrayList<>();
	static {
		File dir = new File("fonts");
		Stream.of(dir.listFiles())
			.forEach(f -> {
				Matcher matcher = RegistryHeroine.PATTERN_FILENAME_BODY.matcher(f.getName());
				if (matcher.matches()) {

					BufferedImage image1;
					try {
						image1 = ImageIO.read(f);
					} catch (IOException e) {
						HLog.processException(e);
						throw new RuntimeException(e);
					}
					fonts.add(new Tuple<>(matcher.group(1), image1));

					// 赤
					fonts.add(new Tuple<>("R" + matcher.group(1), Helpers.copy(image1, 255, 255, 0, 0)));

					// 橙
					fonts.add(new Tuple<>("O" + matcher.group(1), Helpers.copy(image1, 255, 255, 127, 0)));

					// 桃
					fonts.add(new Tuple<>("P" + matcher.group(1), Helpers.copy(image1, 255, 255, 127, 127)));

				}
			});
	}

	/**
	 * 真の文字幅は「実際の画像サイズ-2」。
	 */
	public static Stream<Tuple<String, BufferedImage>> getFonts()
	{
		return fonts.stream();
	}

}
