package mirrg.gubot.noelle;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import jp.hishidama.swing.layout.GroupLayoutUtil;
import mirrg.swing.neon.v1_1.artifacts.logging.FrameLog;

public class GUNoelle
{

	public static void main(String[] args) throws Exception
	{
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		RegistryHeroine.init();

		GUNoelle noelle = new GUNoelle(512, 768);
		noelle.show();
		noelle.start();
	}

	protected int height;
	protected int width;

	protected JFrame frameMain;
	protected JCheckBox checkBoxRunning;
	protected JLabel labelGUFound;
	protected JList<String> listBlackPixels;
	protected JLabel faceLabelTrimed;
	protected JLabel faceLabelGuessed;
	protected JLabel labelStatus;
	protected JTextField textFieldNameHeroine;
	protected JSpinner spinnerSkipLimitMax;
	protected JLabel labelSkipLimit;
	protected JLabel labelSearching;
	protected JLabel labelStatusSearch;
	protected JLabel faceLabelSelected;
	protected JList<String> listHeroines;
	protected JList<String> listButtleClass;

	protected boolean isIconified;

	protected JDialog dialogScreen;
	protected JLabel labelGUScreen;

	protected volatile GU gu;
	protected volatile Heroine heroine;
	protected volatile boolean known;

	public GUNoelle(int height, int width)
	{
		this.height = height;
		this.width = width;

		{
			frameMain = new JFrame("闇のツール");
			try {
				frameMain.setIconImage(ImageIO.read(new File("faces/ノエル.png")));
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			frameMain.addWindowListener(new WindowListener() {

				@Override
				public void windowOpened(WindowEvent e)
				{

				}

				@Override
				public void windowIconified(WindowEvent e)
				{
					isIconified = true;
				}

				@Override
				public void windowDeiconified(WindowEvent e)
				{
					isIconified = false;
				}

				@Override
				public void windowDeactivated(WindowEvent e)
				{

				}

				@Override
				public void windowClosing(WindowEvent e)
				{

				}

				@Override
				public void windowClosed(WindowEvent e)
				{

				}

				@Override
				public void windowActivated(WindowEvent e)
				{

				}

			});

			{
				JMenuBar menubar = new JMenuBar();
				menubar.add(createButton("ログ", e -> {
					new FrameLog(500).setVisible(true);
				}));
				menubar.add(createButton("スクリーン", e -> {
					dialogScreen.pack();
					dialogScreen.setVisible(!dialogScreen.isVisible());
				}));
				frameMain.setJMenuBar(menubar);
			}

			frameMain.setLayout(new CardLayout());
			frameMain.add(createVerticalSplitPane(
				createVerticalBorderPanel(
					createHorizontalBorderPanel(
						null,
						get(() -> {
							labelGUFound = new JLabel();
							setPreferredSize(labelGUFound, 200, 1);
							return labelGUFound;
						}),
						get(() -> {
							checkBoxRunning = new JCheckBox("監視");
							checkBoxRunning.setSelected(true);
							checkBoxRunning.addActionListener(e -> {
								if (checkBoxRunning.isSelected()) {
									start();
								} else {
									stop();
								}
							});
							return checkBoxRunning;
						})),
					get(() -> {
						listBlackPixels = new JList<String>();
						listBlackPixels.setFont(new Font("MS Gothic", Font.PLAIN, listBlackPixels.getFont().getSize()));
						return createScrollPane(listBlackPixels, 260, 80);
					}),
					null),
				createVerticalBorderPanel(
					createHorizontalBorderPanel(
						createPanel(get(() -> {
							faceLabelTrimed = new FaceLabel();
							return faceLabelTrimed;
						})),
						createHorizontalBorderPanel(
							createPanel(get(() -> {
								faceLabelGuessed = new FaceLabel();
								return faceLabelGuessed;
							})),
							get(() -> {
								labelStatus = new JLabel();
								labelStatus.setBackground(Color.white);
								labelStatus.setOpaque(true);
								labelStatus.setPreferredSize(new Dimension(150, 90));
								return labelStatus;
							}),
							null),
						null),
					createVerticalBorderPanel(
						createHorizontalBorderPanel(
							new JLabel("ヒロイン名："),
							get(() -> {
								textFieldNameHeroine = new JTextField(10);
								textFieldNameHeroine.addActionListener(e -> {
									doRegister();
								});
								return textFieldNameHeroine;
							}),
							createButton("登録", e -> {
								doRegister();
							})),
						createVerticalBorderPanel(
							createHorizontalBorderPanel(
								new JLabel("最大反復回数："),
								createHorizontalBorderPanel(
									null,
									createHorizontalBorderPanel(
										null,
										createHorizontalBorderPanel(
											null,
											null,
											get(() -> {
												spinnerSkipLimitMax = new JSpinner(new SpinnerNumberModel(20, 0, 200, 10));
												spinnerSkipLimitMax.setAlignmentX(0.5f);
												((JSpinner.DefaultEditor) spinnerSkipLimitMax.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.CENTER);
												return spinnerSkipLimitMax;
											})),
										get(() -> {
											labelSkipLimit = new JLabel("0");
											labelSkipLimit.setHorizontalAlignment(SwingConstants.CENTER);
											setPreferredSize(labelSkipLimit, 40, 1);
											return labelSkipLimit;
										})),
									get(() -> {
										labelSearching = new JLabel("停止中");
										setPreferredSize(labelSearching, 40, 1);
										return labelSearching;
									})),
								createButton("検索", e -> {
									if (!checkBoxRunning.isSelected()) checkBoxRunning.doClick();
									startSearch();
								})),
							createVerticalBorderPanel(
								get(() -> {
									labelStatusSearch = new JLabel("");
									setPreferredSize(labelStatusSearch, 200, 1);
									return labelStatusSearch;
								}),
								createHorizontalSplitPane(
									createVerticalBorderPanel(
										createPanel(get(() -> {
											faceLabelSelected = new FaceLabel();
											return faceLabelSelected;
										})),
										get(() -> {
											listHeroines = new JList<String>();
											refreshHeroines();
											RegistryHeroine.addListener(h -> {
												refreshHeroines();
											});
											listHeroines.addListSelectionListener(e -> {
												String name = listHeroines.getSelectedValue();
												if (name != null) {
													if (name.equals("None")) {
														faceLabelSelected.setIcon(new ImageIcon(RegistryHeroine.get("黒").image));
													} else {
														faceLabelSelected.setIcon(new ImageIcon(RegistryHeroine.get(name).image));
													}
												} else {
													faceLabelSelected.setIcon(null);
												}
											});
											return createScrollPane(listHeroines, 120, 200);
										}),
										null),
									get(() -> {
										listButtleClass = new JList<String>();
										listButtleClass.setListData(Stream.concat(
											Stream.of("None"),
											Stream.of(RegistryHeroine.getBattleClasses()))
											.toArray(String[]::new));
										return createScrollPane(listButtleClass, 120, 300);
									})),
								null),
							null),
						null),
					null)));

			frameMain.pack();
			frameMain.setLocationByPlatform(true);
			frameMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}

		{
			dialogScreen = new JDialog(frameMain, "スクリーン");

			dialogScreen.setLayout(new CardLayout());
			dialogScreen.add(get(() -> {
				labelGUScreen = new ScreenLabel(width, height);
				return labelGUScreen;
			}));

			dialogScreen.pack();
			dialogScreen.setResizable(false);
			dialogScreen.setLocationByPlatform(true);
			dialogScreen.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		}

		start();
	}

	private void startSearch()
	{
		Thread thread = new Thread(() -> {
			int t = 0;
			int skipLimit = (Integer) spinnerSkipLimitMax.getValue();

			SwingUtilities.invokeLater(() -> {
				labelStatusSearch.setText("");
				labelSearching.setText("実行中");
			});
			try {
				while (true) {
					labelSkipLimit.setText("" + skipLimit);

					try {
						Thread.sleep(20);
					} catch (InterruptedException e1) {
						return;
					}
					t += 20;

					// 最小化時に終わる
					if (isIconified) {
						SwingUtilities.invokeLater(() -> {
							labelStatusSearch.setText("ツール画面が最小化されました。");
						});
						break;
					}

					// GU最小化時に終わる
					if (gu == null) {
						SwingUtilities.invokeLater(() -> {
							labelStatusSearch.setText("GUの画面が最小化されました。");
						});
						break;
					}

					// 黒背景
					if (isBlankProvided()) {
						t = 0;
						continue;
					}

					// 既知ヒロイン（＝飛ばすべきもの）が居た場合
					if (isKnownHeroineProvided()) {

						// キャッチヒロインに指定されている場合終了
						if (listHeroines.getSelectedValuesList().stream()
							.map(o -> (String) o)
							.filter(b -> b.equals(heroine.name))
							.findAny()
							.isPresent()) {
							SwingUtilities.invokeLater(() -> {
								labelStatusSearch.setText("指定のヒロインです。");
							});
							break;
						}

						// キャッチクラスに指定されている場合終了
						if (listButtleClass.getSelectedValuesList().stream()
							.map(o -> (String) o)
							.filter(b -> b.equals(heroine.getButtleClass()))
							.findAny()
							.isPresent()) {
							SwingUtilities.invokeLater(() -> {
								labelStatusSearch.setText("指定のクラスのヒロインです。");
							});
							break;
						}

						// 飛ばしてよい

						if (skipLimit <= 0) { // 回数オーバー
							SwingUtilities.invokeLater(() -> {
								labelStatusSearch.setText("規定回数の検索が終了しました。");
							});
							break;
						}
						skipLimit--;

						gu.next();

						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {
							SwingUtilities.invokeLater(() -> {
								labelStatusSearch.setText("スレッドが中断されました。");
							});
							break;
						}

						t = 0;
						continue;
					}

					// 飛ばすべきものでなく黒画像でもない状況で0.5秒経過
					if (t > 500) {
						SwingUtilities.invokeLater(() -> {
							labelStatusSearch.setText("未知のヒロインです。");
						});
						break;
					}

				}
			} finally {
				SwingUtilities.invokeLater(() -> {
					labelSearching.setText("停止中");
				});
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	private void doRegister()
	{
		String text = textFieldNameHeroine.getText();
		if (text == null || text.isEmpty()) return;
		try {
			RegistryHeroine.register(gu.imageFace, text);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		textFieldNameHeroine.setText("");
	}

	private void refreshHeroines()
	{
		listHeroines.setListData(Stream.concat(
			Stream.of("None"),
			RegistryHeroine.getHeroines()
				.map(h2 -> h2.name))
			.toArray(String[]::new));
	}

	public void show()
	{
		isIconified = false;
		frameMain.setVisible(true);
	}

	private Thread thread;

	protected boolean isRunning()
	{
		return thread != null;
	}

	public void start()
	{
		if (thread != null) return;
		thread = new Thread(() -> {
			while (true) {

				if (!isIconified) update();

				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					break;
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	protected void stop()
	{
		if (thread == null) return;
		thread.interrupt();
		thread = null;
	}

	protected void update()
	{
		BufferedImage image;
		try {
			image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		} catch (HeadlessException | AWTException e1) {
			return;
		}

		int screenH = image.getHeight();
		int screenW = image.getWidth();

		int[][] buffer = new int[screenW][screenH];

		for (int y = 0; y < screenH; y++) {
			for (int x = 0; x < screenW; x++) {
				buffer[x][y] = getBrightness(image, x, y) == 0 ? 1 : 0;
			}
		}

		ArrayList<Island> islands = new ArrayList<>();
		for (int y = 0; y < screenH; y++) {
			for (int x = 0; x < screenW; x++) {

				if (buffer[x][y] == 1) {
					Island island = new Island(x, y);
					island.extract(buffer, x, y);
					islands.add(island);
				}

			}
		}

		// show
		listBlackPixels.setListData(islands.stream()
			.sorted((a, b) -> b.area - a.area)
			.filter(a -> a.area >= 10)
			.map(a -> String.format("(%4d, %4d), %4d x %4d, Area: %d",
				a.left,
				a.top,
				a.getWidth(),
				a.getHeight(),
				a.area))
			.toArray(String[]::new));

		Optional<Island> island = islands.stream()
			.sorted((a, b) -> a.area - b.area)
			.filter(a -> a.getHeight() == height + 2)
			.filter(a -> a.getWidth() == width + 2)
			.findFirst();

		if (island.isPresent()) {
			gu = new GU(
				island.get().left + 1,
				island.get().top + 1,
				image.getSubimage(island.get().left + 1, island.get().top + 1, width, height));

			// show
			labelGUFound.setText(String.format("(%4d, %4d), %4d x %4d, Area: %d",
				island.get().left,
				island.get().top,
				island.get().getWidth(),
				island.get().getHeight(),
				island.get().area));
			labelGUScreen.setIcon(new ImageIcon(gu.image));
			faceLabelTrimed.setIcon(new ImageIcon(gu.imageFace));

			Optional<Heroine> heroine = RegistryHeroine.getHeroines()
				.min((a, b) -> a.getDistance(gu.imageFace) - b.getDistance(gu.imageFace));

			if (heroine.isPresent()) {
				faceLabelGuessed.setIcon(new ImageIcon(heroine.get().image));
				int distance = heroine.get().getDistance(gu.imageFace);
				boolean known = distance < 1500;
				labelStatus.setText(String.format("<html><table>"
					+ "<tr><td>Name</td><td>%s</td></tr>"
					+ "<tr><td>距離</td><td>%s</td></tr>"
					+ "<tr><td>判定</td><td>%s</td></tr>"
					+ "<tr><td>クラス</td><td>%s</td></tr>"
					+ "</table></html>",
					heroine.get().name,
					distance,
					known ? "既知" : "未知",
					heroine.get().getButtleClass()));

				this.heroine = heroine.get();
				this.known = known;

			} else {
				faceLabelGuessed.setIcon(null);
				labelStatus.setText("");
				this.heroine = null;
				this.known = false;
			}

		} else {

			// show
			labelGUFound.setText("Not found");
			labelGUScreen.setIcon(null);
			faceLabelTrimed.setIcon(null);
			faceLabelGuessed.setIcon(null);
			labelStatus.setText("");
			gu = null;
			this.heroine = null;
			this.known = false;

		}

		frameMain.repaint();
	}

	protected boolean isKnownHeroineProvided()
	{
		return heroine != null && known && !heroine.name.equals("黒");
	}

	protected boolean isBlankProvided()
	{
		return heroine != null && known && heroine.name.equals("黒");
	}

	private static int getBrightness(BufferedImage image, int x, int y)
	{
		int rgb = image.getRGB(x, y);
		return (((rgb & 0xff0000) >> 16) + ((rgb & 0xff00) >> 8) + (rgb & 0xff)) / 3;
	}

	private static JSplitPane createHorizontalSplitPane(Component component1, Component component2)
	{
		return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, component1, component2);
	}

	private static JSplitPane createVerticalSplitPane(Component component1, Component component2)
	{
		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, component1, component2);
	}

	private static JPanel createVerticalBorderPanel(Component top, Component middle, Component bottom)
	{
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		if (top != null) panel.add(top, BorderLayout.NORTH);
		if (middle != null) panel.add(middle, BorderLayout.CENTER);
		if (bottom != null) panel.add(bottom, BorderLayout.SOUTH);

		return panel;
	}

	private static JPanel createHorizontalBorderPanel(Component left, Component center, Component right)
	{
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		if (left != null) panel.add(left, BorderLayout.WEST);
		if (center != null) panel.add(center, BorderLayout.CENTER);
		if (right != null) panel.add(right, BorderLayout.EAST);

		return panel;
	}

	private static JPanel createGridPanel(Component[][] grid)
	{
		JPanel panel = new JPanel();

		GroupLayoutUtil g = new GroupLayoutUtil();
		g.setComponents(grid);
		g.setGroupLayoutTo(panel);

		return panel;
	}

	private static <T> T get(Supplier<T> supplier)
	{
		return supplier.get();
	}

	private static void setPreferredSize(JLabel label, int width, int rows)
	{
		label.setPreferredSize(new Dimension(width, label.getFont().getSize() * rows + 6));
	}

	private static class FaceLabel extends ScreenLabel
	{

		public FaceLabel()
		{
			super(64, 64);
		}

	}

	private static class ScreenLabel extends JLabel
	{

		public ScreenLabel(int width, int height)
		{
			setMaximumSize(new Dimension(width, height));
			setMinimumSize(new Dimension(width, height));
			setPreferredSize(new Dimension(width, height));

			setBackground(new Color(32, 32, 32));
			setOpaque(true);
		}

	}

	private static JPanel createPanel(Consumer<JPanel> initializer)
	{
		JPanel panel = new JPanel();
		initializer.accept(panel);
		return panel;
	}

	private static JPanel createPanel(Component... components)
	{
		JPanel panel = new JPanel();
		Stream.of(components)
			.forEach(panel::add);
		return panel;
	}

	private static JButton createButton(String caption, ActionListener listener)
	{
		JButton button = new JButton(caption);
		button.addActionListener(listener);
		return button;
	}

	private static JScrollPane createScrollPane(Component component)
	{
		JScrollPane scrollPane = new JScrollPane(component);
		return scrollPane;
	}

	private static JScrollPane createScrollPane(Component component, int width, int height)
	{
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setPreferredSize(new Dimension(width, height));
		return scrollPane;
	}

}
