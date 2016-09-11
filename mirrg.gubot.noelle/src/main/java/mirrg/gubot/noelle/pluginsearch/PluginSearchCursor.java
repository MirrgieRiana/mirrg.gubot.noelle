package mirrg.gubot.noelle.pluginsearch;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class PluginSearchCursor implements IPluginSearchVisible
{

	private GUNoelle guNoelle;

	public PluginSearchCursor(GUNoelle guNoelle)
	{
		this.guNoelle = guNoelle;
	}

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		if (!guNoelle.guScreen.isPresent()) return new Tuple<>(EnumPluginSearchCondition.STOP, "???");

		if (!guNoelle.guScreen.get().getGameRegion().contains(guNoelle.guScreen.get().getMouseLocation())) {
			return new Tuple<>(EnumPluginSearchCondition.STOP, "カーソルが外れました。");
		} else {
			return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
		}
	}

	@Override
	public String getDescription()
	{
		return "Cursor - カーソルがGU画面から外れたときに終了します。";
	}

}
