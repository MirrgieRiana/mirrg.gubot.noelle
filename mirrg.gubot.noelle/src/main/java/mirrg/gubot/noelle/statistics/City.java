package mirrg.gubot.noelle.statistics;

import mirrg.gubot.noelle.Heroine;

public class City
{

	protected TableCityRecord parent;
	protected int rowIndex;

	public Heroine heroine;
	public int captainExperience;
	public int heroineExperience;
	public double baseExperience;
	public double experienceRatio;
	public int stoneBonus;
	public int gold;
	public int mana;

	public void repaint()
	{
		parent.repaint(this);
	}

}
