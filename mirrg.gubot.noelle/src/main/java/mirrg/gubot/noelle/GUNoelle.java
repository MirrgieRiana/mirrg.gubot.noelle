package mirrg.gubot.noelle;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.AWTException;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import mirrg.gubot.noelle.screen.FactoryGUScreen;
import mirrg.gubot.noelle.screen.FactoryGUScreen.ResponseFind;
import mirrg.gubot.noelle.screen.GUScreen;
import mirrg.gubot.noelle.screen.Island;
import mirrg.gubot.noelle.statistics.City;
import mirrg.gubot.noelle.statistics.TableCityRecord;
import mirrg.helium.standard.hydrogen.struct.Tuple;
import mirrg.helium.standard.hydrogen.struct.Tuple4;
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.FrameLog;
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.HLog;

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
	protected JLabel labelExperimentPoints;
	protected Tuple4<Integer, Integer, Double, Integer> resultExperimentPoints;
	protected JSpinner spinnerSkipLimitMax;
	protected JLabel labelSkipLimit;
	protected JLabel labelSearching;
	protected JCheckBox checkBoxSound;
	protected JCheckBox checkBoxUnknownHeroine;
	protected JCheckBox checkBoxExperienceTrap;
	protected JSpinner spinnerExperienceMax;
	protected JSpinner spinnerExperienceMin;
	protected JSpinner spinnerExperienceRatioMin;
	protected JSpinner spinnerStoneBonusMin;
	protected JLabel faceLabelSelected;
	protected JList<String> listHeroines;
	protected JList<String> listButtleClass;
	protected JLabel labelStatusBar;

	protected boolean isIconified;

	protected JDialog dialogScreen;
	protected JLabel labelGUScreen;

	protected JDialog dialogCityRecord;
	protected TableCityRecord tableCityRecord;
	protected JLabel labelCityRecordCount;

	protected volatile Optional<GUScreen> guScreen = Optional.empty();
	protected volatile long lastSelecting;
	protected volatile boolean isSelecting;
	protected volatile Optional<Heroine> heroine;
	protected volatile boolean known;
	protected volatile boolean phase = false;
	protected volatile City city;

	public GUNoelle(int height, int width)
	{
		this.height = height;
		this.width = width;

		{
			frameMain = new JFrame("闇のツール");

			{
				File file = new File("faces/ノエル.png");
				if (file.isFile()) {
					try {
						frameMain.setIconImage(ImageIO.read(file));
					} catch (IOException e) {
						HLog.processException(e);
					}
				}
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
					{
						JMenuItem menuItem = new JMenuItem("統計情報");
						menuItem.addActionListener(e -> {
							dialogCityRecord.setVisible(!dialogCityRecord.isVisible());
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
					createSplitPaneVertical(
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
						process(createSplitPaneVertical(
							createBorderPanelUp(
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
								get(() -> {
									labelExperimentPoints = new JLabel();
									labelExperimentPoints.setBackground(Color.white);
									labelExperimentPoints.setOpaque(true);
									labelExperimentPoints.setPreferredSize(new Dimension(200, 150));
									return labelExperimentPoints;
								})),
							createBorderPanelUp(
								createBorderPanelLeft(
									new JLabel("最大反復回数："),
									createBorderPanelRight(
										get(() -> {
											spinnerSkipLimitMax = new JSpinner(new SpinnerNumberModel(20, 0, 1000, 10));
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
								createBorderPanelLeft(
									get(() -> {
										checkBoxSound = new JCheckBox("停止時音を鳴らす");
										checkBoxSound.setSelected(true);
										return checkBoxSound;
									}),
									createBorderPanelRight(
										null,
										createButton("試聴", e -> playSound()))),
								createBorderPanelLeft(
									get(() -> {
										checkBoxUnknownHeroine = new JCheckBox("未知ヒロインで停止");
										checkBoxUnknownHeroine.setSelected(true);
										return checkBoxUnknownHeroine;
									}),
									null),
								createBorderPanelLeft(
									get(() -> {
										checkBoxExperienceTrap = new JCheckBox("経験値捕獲を有効にする");
										return checkBoxExperienceTrap;
									}),
									null),
								createBorderPanelLeft(
									new JLabel("　　領主経験値："),
									createBorderPanelRight(
										null,
										get(() -> {
											spinnerExperienceMin = new JSpinner(new SpinnerNumberModel(10000, 0, 10000, 100));
											((JSpinner.DefaultEditor) spinnerExperienceMin.getEditor()).getTextField().setColumns(6);
											return spinnerExperienceMin;
										}),
										new JLabel("～"),
										get(() -> {
											spinnerExperienceMax = new JSpinner(new SpinnerNumberModel(10000, 0, 10000, 100));
											((JSpinner.DefaultEditor) spinnerExperienceMax.getEditor()).getTextField().setColumns(6);
											return spinnerExperienceMax;
										}))),
								createBorderPanelLeft(
									new JLabel("　　最小経験値倍率："),
									createBorderPanelRight(
										null,
										get(() -> {
											spinnerExperienceRatioMin = new JSpinner(new SpinnerNumberModel(5.0, 1.0, 5.0, 0.2));
											((JSpinner.DefaultEditor) spinnerExperienceRatioMin.getEditor()).getTextField().setColumns(2);
											return spinnerExperienceRatioMin;
										}))),
								createBorderPanelLeft(
									new JLabel("　　最小封印石ボーナス："),
									createBorderPanelRight(
										null,
										get(() -> {
											spinnerStoneBonusMin = new JSpinner(new SpinnerNumberModel(3, 0, 3, 1));
											((JSpinner.DefaultEditor) spinnerStoneBonusMin.getEditor()).getTextField().setColumns(2);
											return spinnerStoneBonusMin;
										}))),
								createSplitPaneHorizontal(
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
									})))),
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

		{
			dialogCityRecord = new JDialog(frameMain, "統計情報");

			dialogCityRecord.setLayout(new CardLayout());
			dialogCityRecord.add(createBorderPanelDown(
				get(() -> {
					tableCityRecord = new TableCityRecord();
					return createScrollPane(tableCityRecord, tableCityRecord.preferredWidth, 500);
				}),
				createBorderPanelLeft(
					createButton("リセット", e -> {
						tableCityRecord.reset();
						labelCityRecordCount.setText("" + tableCityRecord.getRowCount());
					}),
					new JLabel("要素数: "),
					labelCityRecordCount = new JLabel("" + 0),
					createBorderPanelRight(
						null,
						get(() -> {
							JFileChooser fileChooser = new JFileChooser(new File("."));
							return createButton("エクスポート", e -> {
								int res = fileChooser.showSaveDialog(dialogCityRecord);
								if (res == JFileChooser.APPROVE_OPTION) {
									try {
										tableCityRecord.export(fileChooser.getSelectedFile());
									} catch (FileNotFoundException e1) {
										HLog.processException(e1);
									}
								}
							});
						})))));

			dialogCityRecord.pack();
			dialogCityRecord.setLocationByPlatform(true);
			dialogCityRecord.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		}

		initThreadUpdateEvent();
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

	public static enum EnumPluginSearchCondition
	{
		SKIPPABLE,
		STOP,
		WAITING,
	}

	public static interface IPluginSearch
	{

		public Tuple<EnumPluginSearchCondition, String> tick(int milis);

		public default void onSkipped()
		{

		}

	}

	/**
	 * Noelle最小化時に終わる
	 */
	public class PluginSearchIconified implements IPluginSearch
	{

		@Override
		public Tuple<EnumPluginSearchCondition, String> tick(int milis)
		{
			if (isIconified) {
				return new Tuple<>(EnumPluginSearchCondition.STOP, "ツール画面が最小化されました。");
			} else {
				return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
			}
		}

	}

	/**
	 * GUを見失ったら終わる
	 */
	public class PluginSearchGUScreen implements IPluginSearch
	{

		@Override
		public Tuple<EnumPluginSearchCondition, String> tick(int milis)
		{
			if (!guScreen.isPresent()) {
				return new Tuple<>(EnumPluginSearchCondition.STOP, "スクリーンを認識できません。");
			} else {
				return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
			}
		}

	}

	public class PluginSearchHeroine implements IPluginSearch
	{

		private int time = 0;

		@Override
		public void onSkipped()
		{
			time = 0;
		}

		@Override
		public Tuple<EnumPluginSearchCondition, String> tick(int milis)
		{

			if (known) {
				if (heroine.get().name.equals("黒")) { // 黒背景

					time = milis;
					return new Tuple<>(EnumPluginSearchCondition.WAITING, null);

				} else { // 既知ヒロインが居た

					// キャッチヒロインに指定されている場合終了
					if (listHeroines.getSelectedValuesList().stream()
						.map(o -> o)
						.filter(b -> b.equals(heroine.get().name))
						.findAny()
						.isPresent()) {
						return new Tuple<>(EnumPluginSearchCondition.STOP, "指定のヒロインです。");
					}

					// キャッチクラスに指定されている場合終了
					if (listButtleClass.getSelectedValuesList().stream()
						.map(o -> o)
						.filter(b -> b.equals(heroine.get().getButtleClass()))
						.findAny()
						.isPresent()) {
						return new Tuple<>(EnumPluginSearchCondition.STOP, "指定のクラスのヒロインです。");
					}

					// 飛ばしてよい
					return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);

				}
			}

			// 0.5秒経過
			if (milis - time > 500) {
				if (isSelecting) {

					// 選択中（飛ばすべきものでも止めるべきものでもなく、黒背景でもない困った状態）
					if (checkBoxUnknownHeroine.isSelected()) {
						return new Tuple<>(EnumPluginSearchCondition.STOP, "未知のヒロインです。");
					} else {
						return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
					}

				} else {

					// 非選択中
					return new Tuple<>(EnumPluginSearchCondition.STOP, "領地選択画面から離れました。");

				}
			}

			return new Tuple<>(EnumPluginSearchCondition.WAITING, null);
		}

	}

	public class PluginSearchExperiencePoints implements IPluginSearch
	{

		@Override
		public Tuple<EnumPluginSearchCondition, String> tick(int milis)
		{
			if (checkBoxExperienceTrap.isSelected()) {
				if (resultExperimentPoints != null) {

					int max = (Integer) spinnerExperienceMax.getModel().getValue();
					int min = (Integer) spinnerExperienceMin.getModel().getValue();
					if (min <= resultExperimentPoints.getX() && resultExperimentPoints.getX() >= max) {
						return new Tuple<>(EnumPluginSearchCondition.STOP, "指定の経験値です。");
					}

					double ratioMin = (Double) spinnerExperienceRatioMin.getModel().getValue();
					if (ratioMin <= resultExperimentPoints.getZ()) {
						return new Tuple<>(EnumPluginSearchCondition.STOP, "指定の倍率です。");
					}

					int stoneBonusMin = (Integer) spinnerStoneBonusMin.getModel().getValue();
					if (stoneBonusMin <= resultExperimentPoints.getW()) {
						return new Tuple<>(EnumPluginSearchCondition.STOP, "指定の封印石ボーナスです。");
					}

					return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
				} else {
					return new Tuple<>(EnumPluginSearchCondition.WAITING, null);
				}
			} else {
				return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
			}
		}

	}

	private void startSearch()
	{
		Thread thread = new Thread(() -> {
			int t = 0;
			int skipLimit = (Integer) spinnerSkipLimitMax.getValue();

			ArrayList<IPluginSearch> plugins = new ArrayList<>();
			{
				plugins.add(new PluginSearchIconified());
				plugins.add(new PluginSearchGUScreen());
				plugins.add(new PluginSearchHeroine());
				plugins.add(new PluginSearchExperiencePoints());
			}

			SwingUtilities.invokeLater(() -> {
				setStatusBar("");
				labelSearching.setText("実行中");
			});

			try {
				while (true) {
					labelSkipLimit.setText("" + skipLimit);

					try {
						Thread.sleep(15);
					} catch (InterruptedException e1) {
						return;
					}
					t += 15;

					boolean skippable = true;
					for (IPluginSearch plugin : plugins) {
						Tuple<EnumPluginSearchCondition, String> res = plugin.tick(t);

						if (res.getX() == EnumPluginSearchCondition.STOP) {
							SwingUtilities.invokeLater(() -> {
								setStatusBar(res.getY());
							});
							return;
						}

						if (res.getX() == EnumPluginSearchCondition.WAITING) {
							skippable = false;
						}

					}

					if (skippable) {

						// 回数オーバーなら終了
						if (skipLimit <= 0) {
							SwingUtilities.invokeLater(() -> {
								setStatusBar("規定回数の検索が終了しました。");
							});
							return;
						}

						// 飛ばす
						{
							skipLimit--;

							guScreen.get().next();

							try {
								Thread.sleep(500);
							} catch (InterruptedException e1) {
								SwingUtilities.invokeLater(() -> {
									setStatusBar("スレッドが中断されました。");
								});
								return;
							}

							plugins.forEach(IPluginSearch::onSkipped);
						}

						t = 0;
						continue;
					}

				}
			} finally {
				SwingUtilities.invokeLater(() -> {
					labelSearching.setText("停止中");
				});

				if (checkBoxSound.isSelected()) playSound();

			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	private void playSound()
	{
		BasicPlayer basicPlayerDie = new BasicPlayer();
		try {
			basicPlayerDie.open(new File("die.mp3"));
			basicPlayerDie.play();
		} catch (BasicPlayerException e) {
			HLog.processException(e);
		}
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
		thread = new ThreadWatchScreen(r -> {

			// ★find時にのみ更新
			{
				listBlackPixels.setListData(r.islands.stream()
					.sorted((a, b) -> b.pixels - a.pixels)
					.filter(a -> a.pixels >= 10)
					.map(Island::toString)
					.toArray(String[]::new));

				if (r.guScreen.isPresent()) {
					labelGUFound.setText(r.island.get().toString());
				} else {
					labelGUFound.setText("Not found");
				}
			}

		}, () -> {

			// ★GU画面がある場合常時更新
			if (guScreen.isPresent()) {
				isSelecting = guScreen.get().isSelecting();
				if (isSelecting) {
					lastSelecting = System.currentTimeMillis();
				} else {
					if (lastSelecting + 1000 < System.currentTimeMillis()) {
						phase = false;
					}
				}

				// 領地選択画面か否か
				labelSelecting.setText(String.format("Distance: %s , %s",
					String.format("%.4f", guScreen.get().getDistanceSelecting()).substring(0, 6),
					isSelecting ? "選択中" : "非選択中"));
				BufferedImage imageFace = guScreen.get().getImageFace();
				BufferedImage imageFaceToMatch = Heroine.getImageToMatch(imageFace);
				faceLabelTrimed.setIcon(new ImageIcon(imageFace));

				// ★領地選択画面である場合常時更新
				if (isSelecting) {

					// ヒロイン候補があるかどうか
					Optional<Tuple<Heroine, Double>> heroine2;
					{
						ArrayList<Tuple<Heroine, Double>> heroines = RegistryHeroine.getHeroines()
							.map(h -> new Tuple<>(h, h.getDistance(imageFaceToMatch, 250)))
							//.filter(h -> h.getY() < 250)
							.sorted((a, b) -> (int) Math.signum(a.getY() - b.getY()))
							.collect(Collectors.toCollection(ArrayList::new));
						heroine2 = heroines.stream()
							.findFirst();

						heroine = heroine2.map(Tuple::getX);
					}

					// ★最有力候補のヒロインについて常時更新
					if (heroine2.isPresent()) {
						faceLabelGuessed.setIcon(new ImageIcon(heroine.get().image));

						// このヒロインについて既知といえるか
						double distance = heroine2.get().getY();
						known = distance < 250;
						if (known) {
							boolean newPhase = !heroine.get().name.equals("黒");

							if (!phase && newPhase) {
								city = new City();
								city.heroine = heroine.get();
								city.time = LocalDateTime.now();
								tableCityRecord.add(city);
								labelCityRecordCount.setText("" + tableCityRecord.getRowCount());
							}

							phase = newPhase;
						}
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
						labelFaceParameters.setText("<html></html>");
					}

					// 経験値文字列取得
					{
						{
							Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>> result = TableCityRecord.parseExperience(guScreen.get().getImage(), 20, 123);
							resultExperimentPoints = result == null ? null : new Tuple4<>(
								result.getX(),
								result.getY(),
								result.getZ().orElse(1.0),
								result.getW().orElse(0));
						}

						if (resultExperimentPoints == null) {
							labelExperimentPoints.setText("<html></html>");
						} else {
							Integer gold = TableCityRecord.parseResources(guScreen.get().getImage(), 189, 32);
							Integer mana = TableCityRecord.parseResources(guScreen.get().getImage(), 189, 64);

							labelExperimentPoints.setText(String.format("<html>"
								+ "<tr><td>領主経験値</td><td>%s</td></tr>"
								+ "<tr><td>ヒロイン経験値</td><td>%s</td></tr>"
								+ "<tr><td>経験値倍率</td><td>%s</td></tr>"
								+ "<tr><td>封印石ボーナス</td><td>%s</td></tr>"
								+ "<tr><td>基礎経験値</td><td>%s</td></tr>"
								+ "<tr><td>ゴールド</td><td>%s</td></tr>"
								+ "<tr><td>マナ</td><td>%s</td></tr>"
								+ "</html>",
								resultExperimentPoints.getX(),
								resultExperimentPoints.getY(),
								resultExperimentPoints.getZ(),
								resultExperimentPoints.getW(),
								resultExperimentPoints.getY() / resultExperimentPoints.getZ(),
								gold,
								mana));
							if (city != null) {
								city.captainExperience = resultExperimentPoints.getX();
								city.heroineExperience = resultExperimentPoints.getY();
								city.experienceRatio = resultExperimentPoints.getZ();
								city.stoneBonus = resultExperimentPoints.getW();
								city.baseExperience = resultExperimentPoints.getY() / resultExperimentPoints.getZ();
								city.gold = gold == null ? 0 : gold;
								city.mana = mana == null ? 0 : mana;
								city.repaint();
							}
						}

					}

				} else {
					heroine = Optional.empty();
					faceLabelGuessed.setIcon(null);
					known = false;
					labelFaceParameters.setText("<html></html>");
					resultExperimentPoints = null;
					labelExperimentPoints.setText("<html></html>");
				}
			} else {
				labelSelecting.setText("No Screen");
				faceLabelTrimed.setIcon(null);
				heroine = Optional.empty();
				faceLabelGuessed.setIcon(null);
				known = false;
				labelFaceParameters.setText("<html></html>");
				resultExperimentPoints = null;
				labelExperimentPoints.setText("<html></html>");
			}

			// ★スクリーンダイアログが出ている間常時更新
			if (guScreen.isPresent()) {
				if (dialogScreen.isVisible()) labelGUScreen.setIcon(new ImageIcon(guScreen.get().getImage()));
			} else {
				if (dialogScreen.isVisible()) labelGUScreen.setIcon(null);
			}

			frameMain.repaint();

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

	private volatile Thread threadUpdateEvent;
	private volatile ArrayList<Runnable> updateEvents = new ArrayList<>();
	private volatile Object lockUpdateEvent = new Object();
	private volatile boolean isUpdateEventProcessing;

	private void initThreadUpdateEvent()
	{
		threadUpdateEvent = new Thread(() -> {
			while (true) {
				ArrayList<Runnable> updateEvents2;
				synchronized (this) {
					updateEvents2 = updateEvents;
					updateEvents = new ArrayList<>();
				}

				if (updateEvents2.size() > 0) {
					updateEvents2.forEach(Runnable::run);
				} else {
					synchronized (lockUpdateEvent) {
						isUpdateEventProcessing = false;
						try {
							lockUpdateEvent.wait();
						} catch (InterruptedException e) {
							break;
						} finally {
							isUpdateEventProcessing = true;
						}
					}
				}
			}
		});
		threadUpdateEvent.setDaemon(true);
		threadUpdateEvent.start();
	}

	private synchronized void addUpdate(Runnable runnable)
	{
		updateEvents.add(runnable);
		synchronized (lockUpdateEvent) {
			lockUpdateEvent.notify();
		}
	}

	public class ThreadWatchScreen extends Thread
	{

		private Consumer<ResponseFind> onFind;
		private Runnable onScreenUpdate;

		public ThreadWatchScreen(Consumer<ResponseFind> onFind, Runnable onScreenUpdate)
		{
			this.onFind = onFind;
			this.onScreenUpdate = onScreenUpdate;
		}

		@Override
		public void run()
		{
			long time = System.currentTimeMillis();
			while (true) {

				if (!isIconified) {
					Optional<GUScreen> guScreen;

					label1:
					{

						// 前回既に取得している場合はvalidateだけして島判定をスルー
						if (GUNoelle.this.guScreen.isPresent()) {
							guScreen = FactoryGUScreen.fromOld(GUNoelle.this.guScreen.get());
							if (guScreen.isPresent()) {
								break label1;
							}
						}

						// 新規にGU画面を取得
						ResponseFind response = FactoryGUScreen.find(width, height);
						guScreen = response.guScreen;

						while (isUpdateEventProcessing) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								break;
							}
						}

						addUpdate(() -> {
							onFind.accept(response);
						});

					}

					while (isUpdateEventProcessing) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							break;
						}
					}

					GUNoelle.this.guScreen = guScreen;
					addUpdate(onScreenUpdate);

				}

				long time2 = System.currentTimeMillis();
				long waitMs = Math.max(time + 15 - time2, 0);
				try {
					Thread.sleep(waitMs);
				} catch (InterruptedException e) {
					break;
				}
				time = System.currentTimeMillis();
			}
		}

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

	private static class FaceLabel extends ScreenLabel
	{

		/**
		 *
		 */
		private static final long serialVersionUID = -1245428170172803632L;

		public FaceLabel()
		{
			super(64, 64);
		}

	}

	private static class ScreenLabel extends JLabel
	{

		/**
		 *
		 */
		private static final long serialVersionUID = 2415051591221706988L;

		public ScreenLabel(int width, int height)
		{
			setMaximumSize(new Dimension(width, height));
			setMinimumSize(new Dimension(width, height));
			setPreferredSize(new Dimension(width, height));

			setBackground(new Color(32, 32, 32));
			setOpaque(true);
		}

	}

	/**
	 * 小さい領域でも60Hzでしか呼び出せない。全画面を取得する場合30Hzに下がる。
	 */
	public static BufferedImage createScreenCapture(int x, int y, int width, int height)
	{
		return ROBOT.createScreenCapture(new Rectangle(x, y, width, height));
	}

	public static BufferedImage createScreenCapture()
	{
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		return createScreenCapture(0, 0, size.width, size.height);
	}

}
