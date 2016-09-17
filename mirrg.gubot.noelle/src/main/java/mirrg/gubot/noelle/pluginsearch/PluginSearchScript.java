package mirrg.gubot.noelle.pluginsearch;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.CardLayout;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.gubot.noelle.IConvertable;
import mirrg.gubot.noelle.RegistryHeroine;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.util.EventPanelSyntax;
import mirrg.helium.compile.oxygen.util.apatite.Formula;
import mirrg.helium.compile.oxygen.util.apatite.PanelApatite;
import mirrg.helium.compile.oxygen.util.apatite.vm1.Syntaxes1;
import mirrg.helium.compile.oxygen.util.apatite.vm1.VM1;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class PluginSearchScript implements IPluginSearchVisible, IConvertable
{

	private GUNoelle guNoelle;
	@XStreamOmitField
	private VMNoelleImpl vm;
	@XStreamOmitField
	private JDialog dialog;
	@XStreamOmitField
	private PanelApatite panelApatite;
	private String src;
	@XStreamOmitField
	private Node<Formula> node;

	public PluginSearchScript(GUNoelle guNoelle)
	{
		this.guNoelle = guNoelle;

		src = "heroine == 'サロメ' || heroine == 'ナノ' || heroine == 'マリシャス'";
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
			dialog.add(get(() -> {
				panelApatite = new PanelApatite(Syntaxes1.root, vm);
				panelApatite.getPanelSyntax().set(src);
				return panelApatite;
			}));

			dialog.pack();
			dialog.setLocationByPlatform(true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}

		node = Syntaxes1.root.parse(src);
		if (node != null && !node.value.validate(vm).isValid()) node = null;

		panelApatite.getPanelSyntax().eventManager.register(EventPanelSyntax.Edit.class, e -> {
			src = e.source;

			node = Syntaxes1.root.parse(src);
			if (node != null && !node.value.validate(vm).isValid()) node = null;
		});
		panelApatite.getPanelSyntax().eventManager.register(EventPanelSyntax.Parsed.class, e -> {
			if (e.timing == EventPanelSyntax.Parsed.TIMING_EDIT) {
				this.node = (Node<Formula>) e.node;
				if (node != null && !node.value.validate(vm).isValid()) node = null;
			}
		});
	}

	@XStreamOmitField
	private boolean occurWaitintException = false;

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		if (!updateUnknown(milis)) new Tuple<>(EnumPluginSearchCondition.WAITING, null);

		if (node == null) return new Tuple<>(EnumPluginSearchCondition.STOP, "スクリプトが不正です。");

		boolean res;
		occurWaitintException = true;
		try {
			res = (boolean) node.value.calculate(vm.createRuntime());
		} catch (Waiting e) {
			return new Tuple<>(EnumPluginSearchCondition.WAITING, null);
		} catch (Exception e) {
			e.printStackTrace();
			return new Tuple<>(EnumPluginSearchCondition.STOP, "ScriptError: " + e.getMessage());
		}
		occurWaitintException = false;

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

	private class VMNoelleImpl extends VM1
	{

		public VMNoelleImpl()
		{
			registerConstant(VM1.STRING, "heroine", "waiting");
			registerConstant(VM1.STRING, "class", "waiting");

			registerConstant(VM1.INTEGER, "captainExp", -4142);
			registerConstant(VM1.INTEGER, "heroineExp", -4142);
			registerConstant(VM1.DOUBLE, "expRatio", -4142.0);
			registerConstant(VM1.INTEGER, "stoneBonus", -4142);
			registerConstant(VM1.DOUBLE, "baseExp", -4142.0);
			registerConstant(VM1.INTEGER, "gold", -4142);
			registerConstant(VM1.INTEGER, "mana", -4142);

			registerConstant(VM1.BOOLEAN, "unknown", false);
		}

		@Override
		public Object getVariableContent(String identifier)
		{
			try {
				return getVariableContentImpl(identifier);
			} catch (Waiting e) {
				if (occurWaitintException) {
					throw e;
				} else {
					return super.getVariableContent(identifier);
				}
			}
		}

		public Object getVariableContentImpl(String identifier)
		{
			if (identifier.equals("heroine")) {
				if (!guNoelle.knownOrBlack) throw waiting;
				if (guNoelle.heroine.get().name.equals("黒")) throw waiting;
				return guNoelle.heroine.get().name;
			}
			if (identifier.equals("class")) {
				if (!guNoelle.knownOrBlack) throw waiting;
				if (guNoelle.heroine.get().name.equals("黒")) throw waiting;
				return RegistryHeroine.buttleClasses.get(guNoelle.heroine.get().name);
			}

			if (identifier.equals("captainExp")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return guNoelle.resultExperimentPoints.getX();
			}
			if (identifier.equals("heroineExp")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return guNoelle.resultExperimentPoints.getY();
			}
			if (identifier.equals("expRatio")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return guNoelle.resultExperimentPoints.getZ();
			}
			if (identifier.equals("stoneBonus")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return guNoelle.resultExperimentPoints.getW();
			}
			if (identifier.equals("baseExp")) {
				if (guNoelle.resultExperimentPoints == null) throw waiting;
				return 1.0 * guNoelle.resultExperimentPoints.getX() / guNoelle.resultExperimentPoints.getZ();
			}
			if (identifier.equals("gold")) {
				if (guNoelle.city == null) throw waiting;
				if (guNoelle.city.gold == null) throw waiting;
				return guNoelle.city.gold;
			}
			if (identifier.equals("mana")) {
				if (guNoelle.city == null) throw waiting;
				if (guNoelle.city.mana == null) throw waiting;
				return guNoelle.city.mana;
			}

			if (identifier.equals("unknown")) {
				if (isUnknown == null) throw waiting;
				return isUnknown;
			}
			return super.getVariableContent(identifier);
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
