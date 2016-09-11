package mirrg.gubot.noelle.pluginsearch;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class PluginSearchWaitExp implements IPluginSearchVisible
{

	private GUNoelle guNoelle;

	public PluginSearchWaitExp(GUNoelle guNoelle)
	{
		this.guNoelle = guNoelle;
	}

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		if (guNoelle.resultExperimentPoints == null) {
			return new Tuple<>(EnumPluginSearchCondition.WAITING, null);
		} else {
			return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
		}
	}

	@Override
	public String getDescription()
	{
		return "WaitExp - 領地経験値の解析が終わるまで待機します。";
	}

}
