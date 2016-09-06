package mirrg.gubot.noelle.statistics;

import static mirrg.gubot.noelle.glyph.ISyntax.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import mirrg.gubot.noelle.glyph.EnumGlyphColor;
import mirrg.gubot.noelle.glyph.Glyph;
import mirrg.gubot.noelle.glyph.ISyntax;
import mirrg.gubot.noelle.glyph.RegistryGlyph;
import mirrg.gubot.noelle.glyph.Result;
import mirrg.helium.standard.hydrogen.struct.Tuple;
import mirrg.helium.standard.hydrogen.struct.Tuple4;
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.HLog;

public class TableCityRecord extends JTable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2381871167541831774L;
	protected DefaultTableModel tableModel;
	public ArrayList<City> cities = new ArrayList<>();

	public TableCityRecord()
	{
		tableModel = new DefaultTableModel();
		setModel(tableModel);

		{
			ArrayList<Tuple<String, Integer>> columns = new ArrayList<>();
			columns.add(new Tuple<>("日付", 70));
			columns.add(new Tuple<>("時刻", 70));
			columns.add(new Tuple<>("秘書", 100));
			columns.add(new Tuple<>("領主経験値", 35));
			columns.add(new Tuple<>("ヒロイン経験値", 40));
			columns.add(new Tuple<>("ベース経験値", 45));
			columns.add(new Tuple<>("経験値倍率", 20));
			columns.add(new Tuple<>("封印石ボーナス", 20));
			columns.add(new Tuple<>("ゴールド", 40));
			columns.add(new Tuple<>("マナ", 40));
			setColumns(columns);
		}
	}

	private void setColumns(ArrayList<Tuple<String, Integer>> columns)
	{
		TableColumnModel cm = getColumnModel();

		columns.forEach(c -> tableModel.addColumn(c.getX()));
		columns.forEach(c -> {
			cm.getColumn(cm.getColumnIndex(c.getX())).setPreferredWidth(c.getY());
		});
	}

	public void add(City city)
	{
		cities.add(city);

		city.parent = this;
		city.rowIndex = tableModel.getRowCount();

		tableModel.setRowCount(tableModel.getRowCount() + 1);
		repaint(city);
	}

	public void reset()
	{
		cities.forEach(c -> c.parent = null);
		cities.clear();

		tableModel.setRowCount(0);
	}

	public void repaint(City city)
	{
		int r = city.rowIndex;
		tableModel.setValueAt(city.time.toLocalDate(), r, 0);
		tableModel.setValueAt(city.time.toLocalTime(), r, 1);
		tableModel.setValueAt(city.heroine.name, r, 2);
		tableModel.setValueAt(city.captainExperience, r, 3);
		tableModel.setValueAt(city.heroineExperience, r, 4);
		tableModel.setValueAt(city.baseExperience, r, 5);
		tableModel.setValueAt(city.experienceRatio, r, 6);
		tableModel.setValueAt(city.stoneBonus, r, 7);
		tableModel.setValueAt(city.gold, r, 8);
		tableModel.setValueAt(city.mana, r, 9);
	}

	@SuppressWarnings("resource")
	public void export(File file) throws FileNotFoundException
	{
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream(file), true, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			HLog.processException(e);
			return;
		}
		out.println("Date,Time,Heroine,CaptainExp,HeroineExp,BaseExp,ExpRatio,StoneBonus,Gold,Mana");
		cities.forEach(c -> {
			out.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
				c.time.toLocalDate(),
				c.time.toLocalTime(),
				c.heroine.name,
				c.captainExperience,
				c.heroineExperience,
				c.baseExperience,
				c.experienceRatio,
				c.stoneBonus,
				c.gold,
				c.mana));
		});
		out.close();
	}

	public static Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>> parseExperience(BufferedImage image, int x, int y)
	{
		RegistryGlyph registry = RegistryGlyph.normal;
		Function<EnumGlyphColor[], ISyntax<Glyph>> decimal = color -> orEx((Glyph) null)
			.or(ch(registry, "0", color))
			.or(ch(registry, "1", color))
			.or(ch(registry, "2", color))
			.or(ch(registry, "3", color))
			.or(ch(registry, "4", color))
			.or(ch(registry, "5", color))
			.or(ch(registry, "6", color))
			.or(ch(registry, "7", color))
			.or(ch(registry, "8", color))
			.or(ch(registry, "9", color));
		ISyntax<Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>>> syntax = se(hash -> new Tuple4<>(
			(Integer) hash.get("1"),
			(Integer) hash.get("2"),
			(Optional<Double>) hash.get("3"),
			(Optional<Integer>) hash.get("4")))
				.and(ch(registry, "領主の獲得可能経験値：", EnumGlyphColor.WHITE))
				.and(na("1", ma(re1(decimal.apply(co(EnumGlyphColor.WHITE, EnumGlyphColor.ORANGE))),
					list -> Integer.parseInt(list.stream()
						.map(g -> g.value)
						.collect(Collectors.joining()), 10))))
				.and(br())
				.and(ch(registry, "ヒロインの獲得可能経験値：", EnumGlyphColor.WHITE))
				.and(na("2", ma(re1(decimal.apply(co(EnumGlyphColor.WHITE, EnumGlyphColor.ORANGE))),
					list -> Integer.parseInt(list.stream()
						.map(g -> g.value)
						.collect(Collectors.joining()), 10))))
				.and(br())
				.and(na("3", op(or((Double) null)
					.or(se(hash -> Double.parseDouble(((Glyph) hash.get("1")).value + "." + ((Glyph) hash.get("2")).value))
						.and(na("1", decimal.apply(co(EnumGlyphColor.ORANGE))))
						.and(ch(registry, ".", EnumGlyphColor.ORANGE))
						.and(na("2", decimal.apply(co(EnumGlyphColor.ORANGE))))
						.and(ch(registry, "倍　経験値ボーナス発生！", EnumGlyphColor.ORANGE))
						.and(br()))
					.or(se(hash -> Double.parseDouble(((Glyph) hash.get("1")).value))
						.and(na("1", decimal.apply(co(EnumGlyphColor.ORANGE))))
						.and(ch(registry, "倍　経験値ボーナス発生！", EnumGlyphColor.ORANGE))
						.and(br())))))
				.and(na("4", op(or((Integer) null)
					.or(se(hash -> 1)
						.and(ch(registry, "封印石獲得率↑", EnumGlyphColor.PINK))
						.and(br()))
					.or(se(hash -> 2)
						.and(ch(registry, "封印石獲得率↑↑", EnumGlyphColor.RED))
						.and(br())))));
		Result<Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>>> result = syntax.match(image, x, y);
		if (result == null) return null;
		return result.value;
	}

	public static Integer parseResources(BufferedImage image, int x, int y)
	{
		RegistryGlyph registry = RegistryGlyph.small;
		ISyntax<Glyph> decimal = orEx((Glyph) null)
			.or(ch(registry, "0", co(EnumGlyphColor.WHITE)))
			.or(ch(registry, "1", co(EnumGlyphColor.WHITE)))
			.or(ch(registry, "2", co(EnumGlyphColor.WHITE)))
			.or(ch(registry, "3", co(EnumGlyphColor.WHITE)))
			.or(ch(registry, "4", co(EnumGlyphColor.WHITE)))
			.or(ch(registry, "5", co(EnumGlyphColor.WHITE)))
			.or(ch(registry, "6", co(EnumGlyphColor.WHITE)))
			.or(ch(registry, "7", co(EnumGlyphColor.WHITE)))
			.or(ch(registry, "8", co(EnumGlyphColor.WHITE)))
			.or(ch(registry, "9", co(EnumGlyphColor.WHITE)));
		ISyntax<Glyph> decimal2 = orEx((Glyph) null)
			.or(decimal)
			.or(se(hash -> (Glyph) hash.get("1"))
				.and(sp(1))
				.and(na("1", decimal)));
		ISyntax<Integer> decimals = ma(re1(decimal2), gs -> Integer.parseInt(gs.stream()
			.map(g -> g.value)
			.collect(Collectors.joining()), 10));
		ISyntax<Integer> syntax = or((Integer) null)
			.or(se(hash -> (Integer) hash.get("1"))
				.and(subPixelize())
				.and(sp(0))
				.and(na("1", decimals)))
			.or(se(hash -> (Integer) hash.get("1"))
				.and(subPixelize())
				.and(sp(8))
				.and(na("1", decimals)))
			.or(se(hash -> (Integer) hash.get("1"))
				.and(subPixelize())
				.and(sp(8 + 9))
				.and(na("1", decimals)))
			.or(se(hash -> (Integer) hash.get("1"))
				.and(subPixelize())
				.and(sp(8 + 9 + 8))
				.and(na("1", decimals)))
			.or(se(hash -> (Integer) hash.get("1"))
				.and(subPixelize())
				.and(sp(8 + 9 + 8 + 9))
				.and(na("1", decimals)))
			.or(se(hash -> (Integer) hash.get("1"))
				.and(subPixelize())
				.and(sp(8 + 9 + 8 + 9 + 8))
				.and(na("1", decimals)));
		Result<Integer> result = syntax.match(image, x, y);
		if (result == null) return null;
		return result.value;
	}

}
