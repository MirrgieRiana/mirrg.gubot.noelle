package mirrg.gubot.noelle.pluginsearch;

import mirrg.helium.standard.hydrogen.struct.Tuple;

public interface IPluginSearch
{

	public Tuple<EnumPluginSearchCondition, String> tick(int milis);

	public default void onStarted()
	{

	}

	public default void onSkipped()
	{

	}

	public default void openDialog()
	{

	}

	public default void closeDialog()
	{

	}

	public default void onDeleted()
	{
		closeDialog();
	}

}
