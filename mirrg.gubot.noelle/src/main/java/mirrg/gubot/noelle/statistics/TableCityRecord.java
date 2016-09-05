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

import mirrg.gubot.noelle.glyph.EnumGlyphColor;
import mirrg.gubot.noelle.glyph.Glyph;
import mirrg.gubot.noelle.glyph.ISyntax;
import mirrg.gubot.noelle.glyph.ISyntax.Result;
import mirrg.gubot.noelle.glyph.RegistryGlyph;
import mirrg.helium.standard.hydrogen.struct.Tuple4;
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.HLog;

public class TableCityRecord extends JTable
{

	protected DefaultTableModel tableModel;
	public ArrayList<City> cities = new ArrayList<>();

	public TableCityRecord()
	{
		tableModel = new DefaultTableModel();
		{
			tableModel.addColumn("秘書");
			tableModel.addColumn("領主経験値");
			tableModel.addColumn("ヒロイン経験値");
			tableModel.addColumn("ベース経験値");
			tableModel.addColumn("経験値倍率");
			tableModel.addColumn("封印石ボーナス");
		}
		setModel(tableModel);
	}

	public void add(City city)
	{
		cities.add(city);

		city.parent = this;
		city.rowIndex = tableModel.getRowCount();

		tableModel.setRowCount(tableModel.getRowCount() + 1);
		repaint(city);
	}

	public void repaint(City city)
	{
		tableModel.setValueAt(city.heroine.name, city.rowIndex, 0);
		tableModel.setValueAt(city.captainExperience, city.rowIndex, 1);
		tableModel.setValueAt(city.heroineExperience, city.rowIndex, 2);
		tableModel.setValueAt(city.baseExperience, city.rowIndex, 3);
		tableModel.setValueAt(city.experienceRatio, city.rowIndex, 4);
		tableModel.setValueAt(city.stoneBonus, city.rowIndex, 5);
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
		out.println("Heroine,CaptainExp,HeroineExp,BaseExp,ExpRatio,StoneBonus");
		cities.forEach(c -> {
			out.println(String.format("%s,%s,%s,%s,%s,%s",
				c.heroine.name,
				c.captainExperience,
				c.heroineExperience,
				c.baseExperience,
				c.experienceRatio,
				c.stoneBonus));
		});
		out.close();
	}

	public static Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>> parseExperience(BufferedImage image, int x, int y)
	{
		Function<EnumGlyphColor[], ISyntax<Glyph>> decimal = color -> orEx((Glyph) null)
			.or(ch(RegistryGlyph.normal, "0", color))
			.or(ch(RegistryGlyph.normal, "1", color))
			.or(ch(RegistryGlyph.normal, "2", color))
			.or(ch(RegistryGlyph.normal, "3", color))
			.or(ch(RegistryGlyph.normal, "4", color))
			.or(ch(RegistryGlyph.normal, "5", color))
			.or(ch(RegistryGlyph.normal, "6", color))
			.or(ch(RegistryGlyph.normal, "7", color))
			.or(ch(RegistryGlyph.normal, "8", color))
			.or(ch(RegistryGlyph.normal, "9", color));
		ISyntax<Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>>> syntax = se(hash -> new Tuple4<>(
			(Integer) hash.get("1"),
			(Integer) hash.get("2"),
			(Optional<Double>) hash.get("3"),
			(Optional<Integer>) hash.get("4")))
				.and(ch(RegistryGlyph.normal, "領主の獲得可能経験値：", EnumGlyphColor.WHITE))
				.and(na("1", ma(re1(decimal.apply(co(EnumGlyphColor.WHITE, EnumGlyphColor.ORANGE))),
					list -> Integer.parseInt(list.stream()
						.map(g -> g.value)
						.collect(Collectors.joining()), 10))))
				.and(br())
				.and(ch(RegistryGlyph.normal, "ヒロインの獲得可能経験値：", EnumGlyphColor.WHITE))
				.and(na("2", ma(re1(decimal.apply(co(EnumGlyphColor.WHITE, EnumGlyphColor.ORANGE))),
					list -> Integer.parseInt(list.stream()
						.map(g -> g.value)
						.collect(Collectors.joining()), 10))))
				.and(br())
				.and(na("3", op(or((Double) null)
					.or(se(hash -> Double.parseDouble(((Glyph) hash.get("1")).value + "." + ((Glyph) hash.get("2")).value))
						.and(na("1", decimal.apply(co(EnumGlyphColor.ORANGE))))
						.and(ch(RegistryGlyph.normal, ".", EnumGlyphColor.ORANGE))
						.and(na("2", decimal.apply(co(EnumGlyphColor.ORANGE))))
						.and(ch(RegistryGlyph.normal, "倍　経験値ボーナス発生！", EnumGlyphColor.ORANGE))
						.and(br()))
					.or(se(hash -> Double.parseDouble(((Glyph) hash.get("1")).value))
						.and(na("1", decimal.apply(co(EnumGlyphColor.ORANGE))))
						.and(ch(RegistryGlyph.normal, "倍　経験値ボーナス発生！", EnumGlyphColor.ORANGE))
						.and(br())))))
				.and(na("4", op(or((Integer) null)
					.or(se(hash -> 1)
						.and(ch(RegistryGlyph.normal, "封印石獲得率↑", EnumGlyphColor.PINK))
						.and(br()))
					.or(se(hash -> 2)
						.and(ch(RegistryGlyph.normal, "封印石獲得率↑↑", EnumGlyphColor.RED))
						.and(br())))));
		Result<Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>>> result = syntax.match(image, x, y);
		if (result == null) return null;
		return result.value;
	}

}
