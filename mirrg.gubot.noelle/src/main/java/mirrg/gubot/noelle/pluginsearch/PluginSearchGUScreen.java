package mirrg.gubot.noelle.pluginsearch;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple;

/**
 * GUを見失ったら終わる
 */
public class PluginSearchGUScreen implements IPluginSearch
{

	private GUNoelle guNoelle;

	public PluginSearchGUScreen(GUNoelle guNoelle)
	{
		this.guNoelle = guNoelle;
	}

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		if (!guNoelle.guScreen.isPresent()) {
			return new Tuple<>(EnumPluginSearchCondition.STOP, "スクリーンを認識できません。");
		} else {
			return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
		}
	}

}
