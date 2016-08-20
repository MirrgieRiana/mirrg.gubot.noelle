package mirrg.gubot.noelle;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
import javax.swing.border.BevelBorder;

import jp.hishidama.swing.layout.GroupLayoutUtil;
import mirrg.gubot.noelle.screen.FactoryGUScreen;
import mirrg.gubot.noelle.screen.FactoryGUScreen.ResponseFind;
import mirrg.gubot.noelle.screen.GUScreen;
import mirrg.gubot.noelle.screen.Island;
import mirrg.struct.hydrogen.v1_0.Tuple;
import mirrg.swing.neon.v1_1.artifacts.logging.FrameLog;
import mirrg.swing.neon.v1_1.artifacts.logging.HLog;

public class GUNoelle
{

	public static Robot ROBOT;
	static {
		try {
			ROBOT = new Robot();
		} catch (AWTException e) {
			HLog.processException(e);
			throw new RuntimeException(e);
		}
	}

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
	protected JList<String> listBlackPixels;
	protected JLabel labelGUFound;
	protected JLabel labelSelecting;
	protected JLabel faceLabelTrimed;
	protected JLabel faceLabelGuessed;
	protected JLabel labelFaceParameters;
	protected JTextField textFieldNameHeroine;
	protected JSpinner spinnerSkipLimitMax;
	protected JLabel labelSkipLimit;
	protected JLabel labelSearching;
	protected JLabel faceLabelSelected;
	protected JList<String> listHeroines;
	protected JList<String> listButtleClass;
	protected JLabel labelStatusBar;

	protected boolean isIconified;

	protected JDialog dialogScreen;
	protected JLabel labelGUScreen;

	protected volatile Optional<GUScreen> guScreen = Optional.empty();
	protected volatile Optional<Heroine> heroine;
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
				JMenuBar menuBar = new JMenuBar();
				{
					JMenu menu = new JMenu("ファイル");
					{
						JMenuItem menuItem = new JMenuItem("スクリーンショットフォルダを開く");
						menuItem.addActionListener(e -> {
							Runtime runtime = Runtime.getRuntime();
							try {
								runtime.exec("explorer \"" + new File("screenshots").getAbsolutePath() + "\"");
							} catch (IOException e2) {
								HLog.processException(e2);
							}
						});
						menu.add(menuItem);
					}
					menuBar.add(menu);
				}
				{
					JMenu menu = new JMenu("ウィンドウ");
					{
						JMenuItem menuItem = new JMenuItem("ログ");
						menuItem.addActionListener(e -> {
							new FrameLog(500).setVisible(true);
						});
						menu.add(menuItem);
					}
					{
						JMenuItem menuItem = new JMenuItem("スクリーン");
						menuItem.addActionListener(e -> {
							dialogScreen.pack();
							dialogScreen.setVisible(!dialogScreen.isVisible());
						});
						menu.add(menuItem);
					}
					menuBar.add(menu);
				}
				menuBar.add(createButton("スクショ", e -> {
					if (!guScreen.isPresent()) {
						setStatusBar("スクリーンが認識できませんでした。");
						return;
					}
					File dir = new File("screenshots");
					prepareDirectory(dir);

					File file = new File(dir, "" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".png");

					try {
						ImageIO.write(guScreen.get().getImage(), "png", file);
					} catch (IOException e1) {
						HLog.processException(e1);
						setStatusBar("スクリーンショットの保存に失敗しました。");
						return;
					}

					setStatusBar("スクショ保存：" + file);
				}));
				menuBar.add(get(() -> {
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
				}));
				frameMain.setJMenuBar(menuBar);
			}

			frameMain.setLayout(new CardLayout());
			frameMain.add(

				createBorderPanelDown(
					createVerticalSplitPane(
						createBorderPanelDown(
							get(() -> {
								listBlackPixels = new JList<>();
								listBlackPixels.setFont(new Font("MS Gothic", Font.PLAIN, listBlackPixels.getFont().getSize()));
								return createScrollPane(listBlackPixels, 260, 80);
							}),
							get(() -> {
								labelGUFound = new JLabel();
								setPreferredSize(labelGUFound, 200, 1);
								labelGUFound.setFont(new Font("MS Gothic", Font.PLAIN, listBlackPixels.getFont().getSize()));
								return labelGUFound;
							}),
							get(() -> {
								labelSelecting = new JLabel();
								labelSelecting.setFont(new Font("MS Gothic", Font.PLAIN, listBlackPixels.getFont().getSize()));
								return labelSelecting;
							})),
						process(createBorderPanelUp(
							createBorderPanelLeft(
								createPanel(get(() -> {
									faceLabelTrimed = new FaceLabel();
									return faceLabelTrimed;
								})),
								createPanel(get(() -> {
									faceLabelGuessed = new FaceLabel();
									return faceLabelGuessed;
								})),
								get(() -> {
									labelFaceParameters = new JLabel();
									labelFaceParameters.setBackground(Color.white);
									labelFaceParameters.setOpaque(true);
									labelFaceParameters.setPreferredSize(new Dimension(150, 90));
									return labelFaceParameters;
								})),
							createBorderPanelLeft(
								new JLabel("ヒロイン名："),
								createBorderPanelRight(
									get(() -> {
										textFieldNameHeroine = new JTextField(10);
										textFieldNameHeroine.addActionListener(e -> {
											doRegister();
										});
										return textFieldNameHeroine;
									}),
									createButton("登録", e -> {
										doRegister();
									}))),
							createBorderPanelLeft(
								new JLabel("最大反復回数："),
								createBorderPanelRight(
									get(() -> {
										spinnerSkipLimitMax = new JSpinner(new SpinnerNumberModel(20, 0, 200, 10));
										spinnerSkipLimitMax.setAlignmentX(0.5f);
										((JSpinner.DefaultEditor) spinnerSkipLimitMax.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.CENTER);
										return spinnerSkipLimitMax;
									}),
									get(() -> {
										labelSkipLimit = new JLabel("0");
										labelSkipLimit.setHorizontalAlignment(SwingConstants.CENTER);
										setPreferredSize(labelSkipLimit, 40, 1);
										return labelSkipLimit;
									}),
									get(() -> {
										labelSearching = new JLabel("停止中");
										setPreferredSize(labelSearching, 40, 1);
										return labelSearching;
									}),
									createButton("検索", e -> {
										if (!checkBoxRunning.isSelected()) checkBoxRunning.doClick();
										startSearch();
									}))),
							createHorizontalSplitPane(
								createBorderPanelUp(
									createPanel(get(() -> {
										faceLabelSelected = new FaceLabel();
										return faceLabelSelected;
									})),
									get(() -> {
										listHeroines = new JList<>();
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
									})),
								get(() -> {
									listButtleClass = new JList<>();
									listButtleClass.setListData(Stream.concat(
										Stream.of("None"),
										Stream.of(RegistryHeroine.getBattleClasses()))
										.toArray(String[]::new));
									return createScrollPane(listButtleClass, 120, 300);
								}))),
							c -> {
								((JComponent) c).setBorder(new BevelBorder(BevelBorder.LOWERED));
								((JComponent) c).setOpaque(true);
							})),
					get(() -> {
						labelStatusBar = new JLabel("");
						setPreferredSize(labelStatusBar, 200, 1);
						return labelStatusBar;
					})));

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

	protected void setStatusBar(String text)
	{
		labelStatusBar.setText(text);
		labelStatusBar.setForeground(new Color(
			(int) (Math.random() * 128),
			(int) (Math.random() * 128),
			(int) (Math.random() * 128)));
	}

	private void startSearch()
	{
		Thread thread = new Thread(() -> {
			int t = 0;
			int skipLimit = (Integer) spinnerSkipLimitMax.getValue();

			SwingUtilities.invokeLater(() -> {
				setStatusBar("");
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
							setStatusBar("ツール画面が最小化されました。");
						});
						break;
					}

					// GU最小化時に終わる
					if (!guScreen.isPresent()) {
						SwingUtilities.invokeLater(() -> {
							setStatusBar("スクリーンを認識できません。");
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
							.map(o -> o)
							.filter(b -> b.equals(heroine.get().name))
							.findAny()
							.isPresent()) {
							SwingUtilities.invokeLater(() -> {
								setStatusBar("指定のヒロインです。");
							});
							break;
						}

						// キャッチクラスに指定されている場合終了
						if (listButtleClass.getSelectedValuesList().stream()
							.map(o -> o)
							.filter(b -> b.equals(heroine.get().getButtleClass()))
							.findAny()
							.isPresent()) {
							SwingUtilities.invokeLater(() -> {
								setStatusBar("指定のクラスのヒロインです。");
							});
							break;
						}

						// 飛ばしてよい

						if (skipLimit <= 0) { // 回数オーバー
							SwingUtilities.invokeLater(() -> {
								setStatusBar("規定回数の検索が終了しました。");
							});
							break;
						}
						skipLimit--;

						guScreen.get().next();

						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {
							SwingUtilities.invokeLater(() -> {
								setStatusBar("スレッドが中断されました。");
							});
							break;
						}

						t = 0;
						continue;
					}

					// 飛ばすべきものでなく黒画像でもない状況で0.5秒経過
					if (t > 500) {
						SwingUtilities.invokeLater(() -> {
							setStatusBar("未知のヒロインです。");
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
			RegistryHeroine.register(guScreen.get().getImageFace(), text);
		} catch (IOException e1) {
			HLog.processException(e1);
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
		label1:
		{

			// 前回既に取得している場合はvalidateだけして島判定をスルー
			if (guScreen.isPresent()) {
				guScreen = FactoryGUScreen.fromOld(guScreen.get());
				if (guScreen.isPresent()) {
					break label1;
				}
			}

			// 新規にGU画面を取得
			ResponseFind response = FactoryGUScreen.find(width, height);
			guScreen = response.guScreen;

			// ★find時にのみ更新
			{
				listBlackPixels.setListData(response.islands.stream()
					.sorted((a, b) -> b.pixels - a.pixels)
					.filter(a -> a.pixels >= 10)
					.map(Island::toString)
					.toArray(String[]::new));

				if (guScreen.isPresent()) {
					labelGUFound.setText(response.island.get().toString());
				} else {
					labelGUFound.setText("Not found");
				}
			}

		}

		// ★GU画面がある場合常時更新
		if (guScreen.isPresent()) {
			boolean isSelecting = guScreen.get().isSelecting();

			// 領地選択画面か否か
			labelSelecting.setText(String.format("Distance: %s , %s",
				String.format("%.4f", guScreen.get().getDistanceSelecting()).substring(0, 6),
				isSelecting ? "選択中" : "非選択中"));
			faceLabelTrimed.setIcon(new ImageIcon(guScreen.get().getImageFace()));

			// ★領地選択画面である場合常時更新
			if (isSelecting) {

				// ヒロイン候補があるかどうか
				Optional<Tuple<Heroine, Double>> heroin2;
				{
					ArrayList<Tuple<Heroine, Double>> heroins = RegistryHeroine.getHeroines()
						.map(h -> new Tuple<>(h, h.getDistance(guScreen.get().getImageFace(), 15)))
						//.filter(h -> h.getY() < 15)
						.sorted((a, b) -> (int) Math.signum(a.getY() - b.getY()))
						.collect(Collectors.toCollection(ArrayList::new));
					heroin2 = heroins.stream()
						.findFirst();

					heroine = heroin2.map(Tuple::getX);
				}

				// ★最有力候補のヒロインについて常時更新
				if (heroin2.isPresent()) {
					faceLabelGuessed.setIcon(new ImageIcon(heroine.get().image));

					// このヒロインについて既知といえるか
					double distance = heroin2.get().getY();
					known = distance < 15;
					labelFaceParameters.setText(String.format("<html><table>"
						+ "<tr><td>Name</td><td>%s</td></tr>"
						+ "<tr><td>距離</td><td>%.6f</td></tr>"
						+ "<tr><td>判定</td><td>%s</td></tr>"
						+ "<tr><td>クラス</td><td>%s</td></tr>"
						+ "</table></html>",
						heroine.get().name,
						distance,
						known ? "既知" : "未知",
						heroine.get().getButtleClass()));

				} else {
					faceLabelGuessed.setIcon(null);
					known = false;
					labelFaceParameters.setText("");
				}
			} else {
				heroine = Optional.empty();
				faceLabelGuessed.setIcon(null);
				known = false;
				labelFaceParameters.setText("");
			}
		} else {
			labelSelecting.setText("No Screen");
			faceLabelTrimed.setIcon(null);
			heroine = Optional.empty();
			faceLabelGuessed.setIcon(null);
			known = false;
			labelFaceParameters.setText("");
		}

		// ★スクリーンダイアログが出ている間常時更新
		if (guScreen.isPresent()) {
			if (dialogScreen.isVisible()) labelGUScreen.setIcon(new ImageIcon(guScreen.get().getImage()));
		} else {
			if (dialogScreen.isVisible()) labelGUScreen.setIcon(null);
		}

		frameMain.repaint();
	}

	protected boolean isKnownHeroineProvided()
	{
		return heroine.isPresent() && known && !heroine.get().name.equals("黒");
	}

	protected boolean isBlankProvided()
	{
		return heroine.isPresent() && known && heroine.get().name.equals("黒");
	}

	public static void prepareDirectory(File dir)
	{
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
	}

	private static Component createHorizontalSplitPane(Component... components)
	{
		return createHorizontalSplitPane(Arrays.asList(components));
	}

	private static Component createHorizontalSplitPane(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
			components.get(0),
			createHorizontalSplitPane(components.subList(1, components.size())));
	}

	private static Component createVerticalSplitPane(Component... components)
	{
		return createVerticalSplitPane(Arrays.asList(components));
	}

	private static Component createVerticalSplitPane(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
			components.get(0),
			createVerticalSplitPane(components.subList(1, components.size())));
	}

	private static Component createBorderPanelUp(Component... components)
	{
		return createBorderPanelUp(Arrays.asList(components));
	}

	private static Component createBorderPanelUp(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(components.get(0), BorderLayout.NORTH);
		panel.add(createBorderPanelUp(components.subList(1, components.size())), BorderLayout.CENTER);

		return panel;
	}

	private static Component createBorderPanelDown(Component... components)
	{
		return createBorderPanelDown(Arrays.asList(components));
	}

	private static Component createBorderPanelDown(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(createBorderPanelDown(components.subList(0, components.size() - 1)), BorderLayout.CENTER);
		panel.add(components.get(components.size() - 1), BorderLayout.SOUTH);

		return panel;
	}

	private static Component createBorderPanelLeft(Component... components)
	{
		return createBorderPanelLeft(Arrays.asList(components));
	}

	private static Component createBorderPanelLeft(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(components.get(0), BorderLayout.WEST);
		panel.add(createBorderPanelLeft(components.subList(1, components.size())), BorderLayout.CENTER);

		return panel;
	}

	private static Component createBorderPanelRight(Component... components)
	{
		return createBorderPanelRight(Arrays.asList(components));
	}

	private static Component createBorderPanelRight(List<Component> components)
	{
		if (components.size() == 1) return components.get(0);
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout(4, 4));
		panel.add(createBorderPanelRight(components.subList(0, components.size() - 1)), BorderLayout.CENTER);
		panel.add(components.get(components.size() - 1), BorderLayout.EAST);

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

	private static <T> T process(T object, Consumer<T> consumer)
	{
		consumer.accept(object);
		return object;
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
