package mirrg.gubot.noelle.pluginsearch;

import java.util.ArrayList;
import java.util.function.Function;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple3;

public class RegistryPluginSearch
{

	public static ArrayList<Tuple3<Function<GUNoelle, IPluginSearchVisible>, Class<?>, String>> factories = new ArrayList<>();
	static {
		factories.add(new Tuple3<>(PluginSearchIconified::new, PluginSearchIconified.class, "Iconified"));
		factories.add(new Tuple3<>(PluginSearchGUScreen::new, PluginSearchGUScreen.class, "GUScreen"));
		factories.add(new Tuple3<>(PluginSearchCursor::new, PluginSearchCursor.class, "Cursor"));
		factories.add(new Tuple3<>(PluginSearchLegacy::new, PluginSearchLegacy.class, "Legacy"));
		factories.add(new Tuple3<>(PluginSearchScript::new, PluginSearchScript.class, "Script"));
		factories.add(new Tuple3<>(PluginSearchWaitExp::new, PluginSearchWaitExp.class, "WaitExp"));
	}

}
