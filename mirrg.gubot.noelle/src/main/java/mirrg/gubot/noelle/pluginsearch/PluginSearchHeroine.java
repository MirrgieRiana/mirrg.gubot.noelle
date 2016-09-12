package mirrg.gubot.noelle.pluginsearch;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import mirrg.gubot.noelle.GUNoelle;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class PluginSearchHeroine implements IPluginSearch
{

	private GUNoelle guNoelle;
	private PluginSearchLegacy parent;

	public PluginSearchHeroine(GUNoelle guNoelle, PluginSearchLegacy parent)
	{
		this.guNoelle = guNoelle;
		this.parent = parent;
	}

	@XStreamOmitField
	private int time = 0;

	@Override
	public void onSkipped()
	{
		time = 0;
	}

	@Override
	public Tuple<EnumPluginSearchCondition, String> tick(int milis)
	{
		if (!guNoelle.guScreen.isPresent()) return new Tuple<>(EnumPluginSearchCondition.STOP, "???");

		if (guNoelle.knownOrBlack) {
			if (guNoelle.heroine.get().name.equals("黒")) { // 黒背景

				time = milis;
				return new Tuple<>(EnumPluginSearchCondition.WAITING, null);

			} else { // 既知ヒロインが居た

				// キャッチヒロインに指定されている場合終了
				if (parent.dialog.listHeroines.getSelectedValuesList().stream()
					.map(o -> o)
					.filter(b -> b.equals(guNoelle.heroine.get().name))
					.findAny()
					.isPresent()) {
					return new Tuple<>(EnumPluginSearchCondition.STOP, "指定のヒロインです。");
				}

				// キャッチクラスに指定されている場合終了
				if (parent.dialog.listButtleClass.getSelectedValuesList().stream()
					.map(o -> o)
					.filter(b -> b.equals(guNoelle.heroine.get().getButtleClass()))
					.findAny()
					.isPresent()) {
					return new Tuple<>(EnumPluginSearchCondition.STOP, "指定のクラスのヒロインです。");
				}

				// 飛ばしてよい
				return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);

			}
		}

		// 0.5秒経過
		if (milis - time > 500) {
			if (guNoelle.isSelecting) {

				// 選択中（飛ばすべきものでも止めるべきものでもなく、黒背景でもない困った状態）
				if (parent.dialog.checkBoxUnknownHeroine.isSelected()) {
					return new Tuple<>(EnumPluginSearchCondition.STOP, "未知のヒロインです。");
				} else {
					return new Tuple<>(EnumPluginSearchCondition.SKIPPABLE, null);
				}

			} else {

				// 非選択中
				return new Tuple<>(EnumPluginSearchCondition.STOP, "領地選択画面から離れました。");

			}
		}

		return new Tuple<>(EnumPluginSearchCondition.WAITING, null);
	}

}
