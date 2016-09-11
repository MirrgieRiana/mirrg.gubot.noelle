package mirrg.gubot.noelle.pluginsearch;

import javax.swing.WindowConstants;

import mirrg.gubot.noelle.GUNoelle;

public class PluginSearchLegacy extends PluginSearchGroup implements IPluginSearchVisible
{

	private DialogPluginLegacy dialog;

	public PluginSearchLegacy(GUNoelle guNoelle)
	{
		dialog = new DialogPluginLegacy();
		dialog.pack();
		dialog.setLocationByPlatform(true);
		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		add(new PluginSearchExperiencePoints(guNoelle, dialog));
		add(new PluginSearchHeroine(guNoelle, dialog));
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
		return "Legacy - 旧式の検索フィルターです。";
	}

}
