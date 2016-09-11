package mirrg.gubot.noelle.pluginsearch;

import javax.swing.WindowConstants;

import mirrg.gubot.noelle.GUNoelle;

public class PluginSearchLegacy extends PluginSearchGroup
{

	private DialogPluginLegacy dialogPluginLegacy;

	public PluginSearchLegacy(GUNoelle guNoelle)
	{
		dialogPluginLegacy = new DialogPluginLegacy();
		dialogPluginLegacy.pack();
		dialogPluginLegacy.setLocationByPlatform(true);
		dialogPluginLegacy.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		add(new PluginSearchExperiencePoints(guNoelle, dialogPluginLegacy));
		add(new PluginSearchHeroine(guNoelle, dialogPluginLegacy));
	}

	@Override
	public void openDialog()
	{
		dialogPluginLegacy.setVisible(true);
	}

}
