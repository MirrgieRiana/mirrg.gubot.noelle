package mirrg.gubot.noelle;

import static mirrg.gubot.noelle.HSwing.*;

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
	protected volatile boolean isSelecting;
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
						Thread.sleep(15);
					} catch (InterruptedException e1) {
						return;
					}
					t += 15;

					// Noelle最小化時に終わる
					if (isIconified) {
						SwingUtilities.invokeLater(() -> {
							setStatusBar("ツール画面が最小化されました。");
						});
						return;
					}

					// GUを見失ったら終わる
					if (!guScreen.isPresent()) {
						SwingUtilities.invokeLater(() -> {
							setStatusBar("スクリーンを認識できません。");
						});
						return;
					}

					// 既知ヒロインが居た場合
					if (known && !heroine.get().name.equals("黒")) {

						// キャッチヒロインに指定されている場合終了
						if (listHeroines.getSelectedValuesList().stream()
							.map(o -> o)
							.filter(b -> b.equals(heroine.get().name))
							.findAny()
							.isPresent()) {
							SwingUtilities.invokeLater(() -> {
								setStatusBar("指定のヒロインです。");
							});
							return;
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
							return;
						}

						// 飛ばしてよい

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
						}

						t = 0;
						continue;
					}

					// 黒背景
					if (known && heroine.get().name.equals("黒")) {
						t = 0;
						continue;
					}

					// 0.5秒経過
					if (t > 500) {
						if (isSelecting) {

							// 選択中（飛ばすべきものでも止めるべきものでもなく、黒背景でもない困った状態）
							SwingUtilities.invokeLater(() -> {
								setStatusBar("未知のヒロインです。");
							});
							return;

						} else {

							// 非選択中
							SwingUtilities.invokeLater(() -> {
								setStatusBar("領地選択画面から離れました。");
							});
							return;

						}
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
