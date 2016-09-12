package mirrg.gubot.noelle.pluginsearch;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class PluginSearchExperiencePoints implements IPluginSearchLegacy
{

	private GUNoelle guNoelle;
	@XStreamOmitField
	private DialogPluginLegacy dialog;

	public PluginSearchExperiencePoints(GUNoelle guNoelle, DialogPluginLegacy dialog)
	{
		this.guNoelle = guNoelle;
		this.dialog = dialog;
	}

	@Override
	public void setDialog(DialogPluginLegacy dialog)
	{
		this.dialog = dialog;
	}

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		if (!guNoelle.guScreen.isPresent()) return new Tuple<>(EnumPluginSearchCondition.STOP, "???");

		if (dialog.checkBoxExperienceTrap.isSelected()) {
			if (guNoelle.resultExperimentPoints != null) {

				int max = (Integer) dialog.spinnerExperienceMax.getModel().getValue();
				int min = (Integer) dialog.spinnerExperienceMin.getModel().getValue();
				if (min <= guNoelle.resultExperimentPoints.getX() && guNoelle.resultExperimentPoints.getX() >= max) {
					return new Tuple<>(EnumPluginSearchCondition.STOP, "指定の経験値です。");
				}

				double ratioMin = (Double) dialog.spinnerExperienceRatioMin.getModel().getValue();
				if (ratioMin <= guNoelle.resultExperimentPoints.getZ()) {
					return new Tuple<>(EnumPluginSearchCondition.STOP, "指定の倍率です。");
				}

				int stoneBonusMin = (Integer) dialog.spinnerStoneBonusMin.getModel().getValue();
				if (stoneBonusMin <= guNoelle.resultExperimentPoints.getW()) {
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
