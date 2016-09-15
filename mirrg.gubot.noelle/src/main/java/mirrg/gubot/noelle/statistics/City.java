package mirrg.gubot.noelle.statistics;

import java.time.LocalDateTime;

import mirrg.gubot.noelle.Heroine;

public class City
{

	protected TableCityRecord parent;
	protected int rowIndex;

	public LocalDateTime time;

	public Heroine heroine;

	public Integer captainExperience;
	public Integer heroineExperience;
	public Double baseExperience;
	public Double experienceRatio;
	public Integer stoneBonus;
	public Integer gold;
	public Integer mana;

	public City(LocalDateTime time)
	{
		this.time = time;
	}

	public void repaint()
	{
		if (parent != null) parent.repaint(this);
	}

	public static String getHeader()
	{
		return "Date,Time,Heroine,CaptainExp,HeroineExp,BaseExp,ExpRatio,StoneBonus,Gold,Mana";
	}

	@Override
	public String toString()
	{
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
			time.toLocalDate(),
			time.toLocalTime(),
			heroine.name,
			captainExperience,
			heroineExperience,
			baseExperience,
			experienceRatio,
			stoneBonus,
			gold,
			mana);
	}

	public void compile()
	{
		if (parent != null) parent.compile(this);
	}

}
