package mirrg.gubot.noelle.pluginsearch;

import java.util.ArrayList;

import mirrg.helium.standard.hydrogen.struct.Tuple;

public class PluginSearchGroup implements IPluginSearch
{

	protected ArrayList<IPluginSearch> plugins = new ArrayList<>();

	public void add(IPluginSearch plugin)
	{
		plugins.add(plugin);
	}

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		boolean skippable = true;
		for (IPluginSearch plugin : plugins) {
			Tuple<EnumPluginSearchCondition, String> res = plugin.tick(milis);

			if (res.getX() == EnumPluginSearchCondition.STOP) return res;

			if (res.getX() == EnumPluginSearchCondition.WAITING) {
				skippable = false;
			}

		}
		if (skippable) {
			return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
		} else {
			return new Tuple<>(EnumPluginSearchCondition.WAITING, null);
		}
	}

	@Override
	public void onSkipped()
	{
		plugins.forEach(IPluginSearch::onSkipped);
	}

	@Override
	public void onStarted()
	{
		plugins.forEach(IPluginSearch::onStarted);
	}

	@Override
	public void openDialog()
	{
		plugins.forEach(IPluginSearch::openDialog);
	}

}
