package mirrg.gubot.noelle.statistics;

import java.time.LocalDateTime;

import mirrg.gubot.noelle.Heroine;

public class City
{

	protected TableCityRecord parent;
	protected int rowIndex;

	public Heroine heroine;
	public LocalDateTime time;

	public int captainExperience;
	public int heroineExperience;
	public double baseExperience;
	public double experienceRatio;
	public int stoneBonus;
	public int gold;
	public int mana;

	public void repaint()
	{
		if (parent != null) parent.repaint(this);
	}

}
