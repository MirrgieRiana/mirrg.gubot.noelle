package mirrg.image.analyze.overlay.beryllium;

import static javax.imageio.ImageIO.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Main
{

	public static void main(String[] argv) throws Exception
	{
		File dir = new File("workspace");

		ArrayList<ImagePair> pairs = new ArrayList<>();

		for (int i = 0;; i++) {
			File file1 = new File(dir, String.format("%03db.png", i));
			File file2 = new File(dir, String.format("%03do.png", i));
			if (file1.isFile()) {
				if (file2.isFile()) {
					pairs.add(new ImagePair(read(file1), read(file2)));
					continue;
				}
			}
			break;
		}

		BufferedImage image5 = AnalyzerBeryllium.analyze(pairs.stream().toArray(ImagePair[]::new));

		write(image5, "png", new File(dir, "out.png"));
	}

}
