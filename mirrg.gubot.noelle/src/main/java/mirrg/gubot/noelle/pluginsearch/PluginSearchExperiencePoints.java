package mirrg.gubot.noelle.pluginsearch;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class PluginSearchExperiencePoints implements IPluginSearch
{

	private GUNoelle guNoelle;
	private PluginSearchLegacy parent;

	public PluginSearchExperiencePoints(GUNoelle guNoelle, PluginSearchLegacy parent)
	{
		this.guNoelle = guNoelle;
		this.parent = parent;
	}

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		if (!guNoelle.guScreen.isPresent()) return new Tuple<>(EnumPluginSearchCondition.STOP, "???");

		if (parent.dialog.checkBoxExperienceTrap.isSelected()) {
			if (guNoelle.resultExperimentPoints != null) {

				int max = (Integer) parent.dialog.spinnerExperienceMax.getModel().getValue();
				int min = (Integer) parent.dialog.spinnerExperienceMin.getModel().getValue();
				if (min <= guNoelle.resultExperimentPoints.getX() && guNoelle.resultExperimentPoints.getX() >= max) {
					return new Tuple<>(EnumPluginSearchCondition.STOP, "指定の経験値です。");
				}

				double ratioMin = (Double) parent.dialog.spinnerExperienceRatioMin.getModel().getValue();
				if (ratioMin <= guNoelle.resultExperimentPoints.getZ()) {
					return new Tuple<>(EnumPluginSearchCondition.STOP, "指定の倍率です。");
				}

				int stoneBonusMin = (Integer) parent.dialog.spinnerStoneBonusMin.getModel().getValue();
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
