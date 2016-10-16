package mirrg.gubot.noelle.pluginsearch;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.CardLayout;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import mirrg.gubot.noelle.FaceLabel;
import mirrg.gubot.noelle.RegistryHeroine;

public class DialogPluginLegacy extends JDialog
{

	/**
	 *
	 */
	private static final long serialVersionUID = 4495118153371660785L;
	protected JCheckBox checkBoxUnknownHeroine;
	protected JCheckBox checkBoxExperienceTrap;
	protected JSpinner spinnerExperienceMax;
	protected JSpinner spinnerExperienceMin;
	protected JSpinner spinnerExperienceRatioMin;
	protected JSpinner spinnerStoneBonusMin;
	protected JLabel faceLabelSelected;
	protected JList<String> listHeroines;
	protected JList<String> listButtleClass;

	protected static class Data
	{
		protected boolean checkBoxUnknownHeroine;
		protected boolean checkBoxExperienceTrap;
		protected int spinnerExperienceMax;
		protected int spinnerExperienceMin;
		protected double spinnerExperienceRatioMin;
		protected int spinnerStoneBonusMin;
		protected List<String> listHeroines;
		protected List<String> listButtleClass;
	}

	public Data getData()
	{
		Data data = new Data();

		data.checkBoxUnknownHeroine = checkBoxUnknownHeroine.isSelected();
		data.checkBoxExperienceTrap = checkBoxExperienceTrap.isSelected();
		data.spinnerExperienceMax = (Integer) spinnerExperienceMax.getValue();
		data.spinnerExperienceMin = (Integer) spinnerExperienceMin.getValue();
		data.spinnerExperienceRatioMin = (Double) spinnerExperienceRatioMin.getValue();
		data.spinnerStoneBonusMin = (Integer) spinnerStoneBonusMin.getValue();
		data.listHeroines = listHeroines.getSelectedValuesList();
		data.listButtleClass = listButtleClass.getSelectedValuesList();

		return data;
	}

	public void setData(Data data)
	{
		checkBoxUnknownHeroine.setSelected(data.checkBoxUnknownHeroine);
		checkBoxExperienceTrap.setSelected(data.checkBoxExperienceTrap);
		spinnerExperienceMax.setValue(data.spinnerExperienceMax);
		spinnerExperienceMin.setValue(data.spinnerExperienceMin);
		spinnerExperienceRatioMin.setValue(data.spinnerExperienceRatioMin);
		spinnerStoneBonusMin.setValue(data.spinnerStoneBonusMin);
		data.listHeroines.forEach(o -> listHeroines.setSelectedValue(o, true));
		data.listButtleClass.forEach(o -> listButtleClass.setSelectedValue(o, true));
		updateLabel();
	}

	public DialogPluginLegacy(JFrame owner)
	{
		super(owner);
		init();
	}

	public DialogPluginLegacy(JDialog owner)
	{
		super(owner);
		init();
	}

	private void init()
	{
		setLayout(new CardLayout());
		add(createBorderPanelDown(
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
							updateLabel();
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
				}))));
	}

	private void updateLabel()
	{
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
	}

	private void refreshHeroines()
	{
		listHeroines.setListData(Stream.concat(
			Stream.of("None"),
			RegistryHeroine.getHeroines()
				.map(h2 -> h2.name))
			.toArray(String[]::new));
	}

}
