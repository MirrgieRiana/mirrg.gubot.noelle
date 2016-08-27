package mirrg.image.analyze.overlay.beryllium;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Main
{

	/**
	 * 2つ以上の「背景とオーバーレイ付き画像の組」からオーバーレイ画像を計算する。
	 */
	public static void main(String[] argv) throws Exception
	{
		File dir = new File("workspace");

		ArrayList<ImagePair> pairs = new ArrayList<>();

		for (int i = 0;; i++) {
			File file1 = new File(dir, String.format("%03db.png", i));
			File file2 = new File(dir, String.format("%03do.png", i));
			if (file1.isFile()) {
				if (file2.isFile()) {
					pairs.add(new ImagePair(ImageIO.read(file1), ImageIO.read(file2)));
					continue;
				}
			}
			break;
		}

		BufferedImage dest = AnalyzerBeryllium.analyze(pairs.stream().toArray(ImagePair[]::new));

		ImageIO.write(dest, "png", new File(dir, "out1.png"));
	}

}
