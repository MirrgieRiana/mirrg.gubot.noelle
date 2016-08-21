package mirrg.gubot.noelle.screen;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Optional;

import mirrg.gubot.noelle.GUNoelle;

public class FactoryGUScreen
{

	public static Optional<GUScreen> fromOld(GUScreen guOld)
	{
		GUScreenFromOld guScreen = new GUScreenFromOld(guOld.screenX, guOld.screenY, guOld.width, guOld.height);

		if (guScreen.validate()) {
			return Optional.of(guScreen);
		} else {
			return Optional.empty();
		}
	}

	public static class ResponseFind
	{

		public ArrayList<Island> islands;
		public Optional<Island> island;
		public Optional<GUScreen> guScreen;

	}

	public static ResponseFind find(int width, int height)
	{
		ResponseFind response = new ResponseFind();

		// 全画面スクショ
		BufferedImage image = GUNoelle.createScreenCapture();

		int screenH = image.getHeight();
		int screenW = image.getWidth();

		// 島分布
		int[][] buffer = new int[screenW][screenH];
		for (int y = 0; y < screenH; y++) {
			for (int x = 0; x < screenW; x++) {
				buffer[x][y] = (getBrightness(image, x, y) & 0xffffff) == 0 ? 1 : 0;
			}
		}

		// 島リスト
		response.islands = new ArrayList<>();
		for (int y = 0; y < screenH; y++) {
			for (int x = 0; x < screenW; x++) {

				if (buffer[x][y] == 1) {
					Island island = new Island(x, y);
					island.extract(buffer, x, y);
					response.islands.add(island);
				}

			}
		}

		// 適切な島
		response.island = response.islands.stream()
			.filter(a -> a.getHeight() == height + 2)
			.filter(a -> a.getWidth() == width + 2)
			.findFirst();

		if (response.island.isPresent()) {
			GUScreenFromFind guScreen = new GUScreenFromFind(
				response.island.get().left + 1, response.island.get().top + 1, width, height, image);
			if (guScreen.validate()) {
				response.guScreen = Optional.of(guScreen);
			} else {
				response.guScreen = Optional.empty();
			}
		} else {
			response.guScreen = Optional.empty();
		}

		return response;
	}

	private static int getBrightness(BufferedImage image, int x, int y)
	{
		int rgb = image.getRGB(x, y);
		return (((rgb >> 16) & 0xff) + ((rgb >> 8) & 0xff) + (rgb & 0xff)) / 3;
	}

}
