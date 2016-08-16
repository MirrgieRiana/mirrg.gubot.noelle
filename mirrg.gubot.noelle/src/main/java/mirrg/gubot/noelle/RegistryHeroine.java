package mirrg.gubot.noelle;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import mirrg.swing.neon.v1_1.artifacts.logging.HLog;

public class RegistryHeroine
{

	public static final Pattern PATTERN_FILENAME_BODY = Pattern.compile("(.*)\\.[^.]*");

	public static void init()
	{

		// フォルダ確保
		File dir = new File("faces");
		if (!dir.isDirectory()) {
			if (dir.exists()) {
				RuntimeException e = new RuntimeException("This is not a directory: " + dir);
				HLog.processException(e);
				throw e;
			} else {
				if (!dir.mkdir()) {
					RuntimeException e = new RuntimeException("Failed to create directory: " + dir);
					HLog.processException(e);
					throw e;
				}
			}
		}

		// 画像取得
		Stream.of(dir.listFiles())
			.forEach(file -> {
				Matcher matcher = PATTERN_FILENAME_BODY.matcher(file.getName());
				if (matcher.matches()) {

					try {
						add(new Heroine(ImageIO.read(file), matcher.group(1)));
					} catch (IOException e) {
						HLog.processException(e);
						//throw new RuntimeException(e);
					}

				}
			});

		// 戦闘クラス取得
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("data.csv")), "utf-8"));
			reader.lines()
				.map(s -> s.split(","))
				.forEach(r -> buttleClasses.put(r[0], r[1]));
			reader.close();
		} catch (IOException e) {
			HLog.processException(e);
			throw new RuntimeException(e);
		}

	}

	private static Hashtable<String, Heroine> heroines = new Hashtable<>();
	public static Hashtable<String, String> buttleClasses = new Hashtable<>();
	private static ArrayList<Consumer<Heroine>> listeners = new ArrayList<>();

	private static void add(Heroine heroine)
	{
		heroines.put(heroine.name, heroine);
		listeners.forEach(a -> a.accept(heroine));
	}

	public static void addListener(Consumer<Heroine> listener)
	{
		listeners.add(listener);
	}

	public static Heroine get(String name)
	{
		return heroines.get(name);
	}

	public static Heroine register(BufferedImage image, String name) throws IOException
	{
		Heroine heroine = new Heroine(image, name);
		heroine.save();
		add(heroine);
		return heroine;
	}

	public static Stream<Heroine> getHeroines()
	{
		return heroines.values().stream()
			.sorted((a, b) -> a.name.compareTo(b.name));
	}

	public static String[] getBattleClasses()
	{
		return buttleClasses.values().stream()
			.distinct()
			.sorted()
			.toArray(String[]::new);
	}

}
