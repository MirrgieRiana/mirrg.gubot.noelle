package mirrg.gubot.noelle.pluginsearch;

import javax.swing.WindowConstants;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.gubot.noelle.IConvertable;
import mirrg.gubot.noelle.pluginsearch.DialogPluginLegacy.Data;

public class PluginSearchLegacy extends PluginSearchGroup<IPluginSearchLegacy> implements IPluginSearchVisible, IConvertable
{

	@XStreamOmitField
	private DialogPluginLegacy dialog;

	public PluginSearchLegacy(GUNoelle guNoelle)
	{
		init2();

		add(new PluginSearchExperiencePoints(guNoelle, dialog));
		add(new PluginSearchHeroine(guNoelle, dialog));
	}

	private void init2()
	{
		dialog = new DialogPluginLegacy();
		dialog.pack();
		dialog.setLocationByPlatform(true);
		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
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

	private Data data;

	@Override
	public void beforeMarshal()
	{
		data = dialog.getData();
	}

	@Override
	public void afterUnmarshal()
	{
		init2();
		dialog.setData(data);

		plugins.forEach(p -> p.setDialog(dialog));
	}

}
