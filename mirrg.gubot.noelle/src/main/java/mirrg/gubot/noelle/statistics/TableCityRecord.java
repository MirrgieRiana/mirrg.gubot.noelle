package mirrg.gubot.noelle.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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

	public void export(File file) throws FileNotFoundException
	{
		PrintStream out = new PrintStream(new FileOutputStream(file));
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

}
