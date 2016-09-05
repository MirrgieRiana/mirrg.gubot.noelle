package mirrg.gubot.noelle.glyph;

public class Result<T> implements Comparable<Result<T>>
{

	public final T value;
	public final int x;
	public final int x2;
	public final int y;
	public final boolean isFixed;
	public final double distanceSq;

	public Result(T value, int x, int x2, int y, boolean isFixed, double distanceSq)
	{
		this.value = value;
		this.x = x;
		this.x2 = x2;
		this.y = y;
		this.isFixed = isFixed;
		this.distanceSq = distanceSq;
	}

	@Override
	public int compareTo(Result<T> o)
	{
		if (distanceSq > o.distanceSq) return 1;
		if (distanceSq < o.distanceSq) return -1;
		return 0;
	}

}
