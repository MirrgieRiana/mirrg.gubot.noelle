package mirrg.gubot.noelle.pluginsearch;

import java.util.ArrayList;
import java.util.function.Function;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class RegisterPluginSearch
{

	public static ArrayList<Tuple<Function<GUNoelle, IPluginSearchVisible>, String>> factories = new ArrayList<>();
	static {
		factories.add(new Tuple<>(PluginSearchIconified::new, "Iconified"));
		factories.add(new Tuple<>(PluginSearchGUScreen::new, "GUScreen"));
		factories.add(new Tuple<>(PluginSearchCursor::new, "Cursor"));
		factories.add(new Tuple<>(PluginSearchLegacy::new, "Legacy"));
	}

}
