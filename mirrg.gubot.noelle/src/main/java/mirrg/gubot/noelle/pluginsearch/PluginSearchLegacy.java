package mirrg.gubot.noelle.pluginsearch;

import javax.swing.WindowConstants;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.gubot.noelle.IConvertable;
import mirrg.gubot.noelle.pluginsearch.DialogPluginLegacy.Data;

public class PluginSearchLegacy extends PluginSearchGroup implements IPluginSearchVisible, IConvertable
{

	private GUNoelle guNoelle;
	@XStreamOmitField
	protected DialogPluginLegacy dialog;

	public PluginSearchLegacy(GUNoelle guNoelle)
	{
		this.guNoelle = guNoelle;

		init2();

		add(new PluginSearchExperiencePoints(guNoelle, this));
		add(new PluginSearchHeroine(guNoelle, this));
	}

	private void init2()
	{
		dialog = new DialogPluginLegacy(guNoelle.frameMain);
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
	}

}
