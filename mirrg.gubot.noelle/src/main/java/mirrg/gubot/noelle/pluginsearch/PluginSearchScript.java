package mirrg.gubot.noelle.pluginsearch;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.CardLayout;
import java.io.IOException;
import java.util.stream.Stream;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.WindowConstants;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.gubot.noelle.IConvertable;
import mirrg.gubot.noelle.RegistryHeroine;
import mirrg.gubot.noelle.script.IFormulaBoolean;
import mirrg.gubot.noelle.script.ScriptNoelle;
import mirrg.gubot.noelle.script.VMNoelle;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.util.PanelSyntax;
import mirrg.helium.standard.hydrogen.struct.Tuple;
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.HLog;

public class PluginSearchScript implements IPluginSearchVisible, IConvertable
{

	private GUNoelle guNoelle;
	@XStreamOmitField
	private VMNoelleImpl vm;
	@XStreamOmitField
	private JDialog dialog;
	@XStreamOmitField
	private PanelSyntax panelSyntax;
	private String src;
	@XStreamOmitField
	private Node<IFormulaBoolean> node;

	public PluginSearchScript(GUNoelle guNoelle)
	{
		this.guNoelle = guNoelle;

		System.out.println(src);
		src = ScriptNoelle.sampleSrc;
		System.out.println(src);
		init2();
	}

	@Override
	public void afterUnmarshal()
	{
		init2();
	}

	private void init2()
	{
		vm = new VMNoelleImpl();

		dialog = new JDialog(guNoelle.frameMain, "闇のスクリプト");
		{
			dialog.setLayout(new CardLayout());
			dialog.add(createSplitPaneVertical(
				createScrollPane(get(() -> {
					JEditorPane textPane = new JEditorPane();
					textPane.setEditable(false);
					try {
						textPane.setPage(getClass().getResource("help.html"));
					} catch (IOException e) {
						HLog.processException(e);
					}
					return textPane;
				}), 200, 100),
				panelSyntax = new PanelSyntax(ScriptNoelle.getParser(vm), src)));

			dialog.pack();
			dialog.setLocationByPlatform(true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}

		node = ScriptNoelle.getParser(vm).parse(src);

		panelSyntax.eventManager.register(String.class, s -> {
			Node<IFormulaBoolean> node = ScriptNoelle.getParser(vm).parse(s);
			if (node != null) {
				this.node = node;
				src = s;
			}
		});
	}

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		if (!updateUnknown(milis)) new Tuple<>(EnumPluginSearchCondition.WAITING, null);

		if (node == null) return new Tuple<>(EnumPluginSearchCondition.STOP, "スクリプトが不正です。");

		boolean res;
		try {
			res = node.value.calculate();
		} catch (Waiting e) {
			return new Tuple<>(EnumPluginSearchCondition.WAITING, null);
		} catch (Exception e) {
			return new Tuple<>(EnumPluginSearchCondition.STOP, "ScriptError: " + e.getMessage());
		}

		if (res) {
			return new Tuple<>(EnumPluginSearchCondition.STOP, "スクリプトの条件にマッチしました。");
		} else {
			return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
		}
	}

	@XStreamOmitField
	private int time = 0;
	@XStreamOmitField
	private Boolean isUnknown;

	@Override
	public void onSkipped()
	{
		time = 0;
		isUnknown = null;
	}

	private boolean updateUnknown(int milis)
	{
		if (guNoelle.knownOrBlack) {
			// 既知か黒
			isUnknown = false;
		} else if (milis - time > 500) {
			// 未知の状態でしばらく経った
			isUnknown = true;
		} else {
			// 一瞬未知だがまだ断定できない
			isUnknown = null;
			return false;
		}

		return true;
	}

	public static Waiting waiting = new Waiting();

	private class VMNoelleImpl extends VMNoelle
	{

		@Override
		public Object getVariable(String name)
		{
			if (name.equals("heroine")) {
				if (!guNoelle.knownOrBlack) throw waiting;
				if (guNoelle.heroine.get().name.equals("黒")) throw waiting;
				return guNoelle.heroine.get().name;
			}
			if (name.equals("class")) {
				if (!guNoelle.knownOrBlack) throw waiting;
				if (guNoelle.heroine.get().name.equals("黒")) throw waiting;
				return RegistryHeroine.buttleClasses.get(guNoelle.heroine.get().name);
			}
			if (name.equals("captainExp")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return (double) guNoelle.resultExperimentPoints.getX();
			}
			if (name.equals("heroineExp")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return (double) guNoelle.resultExperimentPoints.getY();
			}
			if (name.equals("expRatio")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return (double) guNoelle.resultExperimentPoints.getZ();
			}
			if (name.equals("stoneBonus")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return (double) guNoelle.resultExperimentPoints.getW();
			}
			if (name.equals("baseExp")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return 1.0 * guNoelle.resultExperimentPoints.getX() / guNoelle.resultExperimentPoints.getZ();
			}
			if (name.equals("gold")) {
				if (guNoelle.city == null) throw waiting;
				if (guNoelle.city.gold == null) throw waiting;
				return (double) guNoelle.city.gold;
			}
			if (name.equals("mana")) {
				if (guNoelle.city == null) throw waiting;
				if (guNoelle.city.mana == null) throw waiting;
				return (double) guNoelle.city.mana;
			}
			if (name.equals("unknown")) {
				if (isUnknown == null) throw waiting;
				return isUnknown;
			}
			if (name.equals("true")) {
				return true;
			}
			if (name.equals("false")) {
				return false;
			}
			throw new RuntimeException("No such variable: " + name);
		}

		@Override
		public Stream<String> getVariableNamesForString()
		{
			return Stream.of(
				"heroine",
				"class");
		}

		@Override
		public Stream<String> getVariableNamesForDouble()
		{
			return Stream.of(
				"captainExp",
				"heroineExp",
				"expRatio",
				"stoneBonus",
				"baseExp",
				"gold",
				"mana");
		}

		@Override
		public Stream<String> getVariableNamesForBoolean()
		{
			return Stream.of(
				"unknown",
				"true",
				"false");
		}

	}

	public static class Waiting extends RuntimeException
	{

		public Waiting()
		{
			super();
		}

		public Waiting(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
		{
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public Waiting(String message, Throwable cause)
		{
			super(message, cause);
		}

		public Waiting(String message)
		{
			super(message);
		}

		public Waiting(Throwable cause)
		{
			super(cause);
		}

	}

	@Override
	public void openDialog()
	{
		dialog.setVisible(true);
	}

	@Override
	public void closeDialog()
	{
		dialog.dispose();
	}

	@Override
	public String getDescription()
	{
		return "Script - 高度な検索フィルターです。";
	}

}
