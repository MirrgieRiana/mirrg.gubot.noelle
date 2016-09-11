package mirrg.gubot.noelle.pluginsearch;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple;

/**
 * Noelle最小化時に終わる
 */
public class PluginSearchIconified implements IPluginSearch
{

	private GUNoelle guNoelle;

	public PluginSearchIconified(GUNoelle guNoelle)
	{
		this.guNoelle = guNoelle;
	}

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		if (guNoelle.isIconified) {
			return new Tuple<>(EnumPluginSearchCondition.STOP, "ツール画面が最小化されました。");
		} else {
			return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
		}
	}

}
