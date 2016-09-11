package mirrg.gubot.noelle.pluginsearch;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class PluginSearchIconified implements IPluginSearchVisible
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

	@Override
	public String getDescription()
	{
		return "Iconified - ツールが最小化されたときに終了します。";
	}

}
