package mirrg.gubot.noelle.screen;

import java.util.ArrayList;

public class Island
{

	public int left;
	public int right;
	public int top;
	public int bottom;

	public int pixels;

	public Island(int x, int y)
	{
		this.left = x;
		this.right = x;
		this.top = y;
		this.bottom = y;
	}

	public int getWidth()
	{
		return right - left + 1;
	}

	public int getHeight()
	{
		return bottom - top + 1;
	}

	private void addX(int x)
	{
		if (x < left) left = x;
		if (x > right) right = x;
	}

	private void addY(int y)
	{
		if (y < top) top = y;
		if (y > bottom) bottom = y;
	}

	private ArrayList<Integer> xs;
	private ArrayList<Integer> ys;
	private int[][] buffer;

	public synchronized void extract(int[][] buffer, int x, int y)
	{
		xs = new ArrayList<>();
		ys = new ArrayList<>();
		this.buffer = buffer;

		xs.add(x);
		ys.add(y);

		for (int i = 0; i < xs.size(); i++) {
			deleteIsland(xs.get(i), ys.get(i));
		}

	}

	private void deleteIsland(int x, int y)
	{
		if (buffer[x][y] == 1) {

			pixels++;
			addX(x);
			addY(y);

			buffer[x][y] = 0;

			if (x > 0) {
				xs.add(x - 1);
				ys.add(y);
			}
			if (y > 0) {
				xs.add(x);
				ys.add(y - 1);
			}
			if (x < buffer.length - 1) {
				xs.add(x + 1);
				ys.add(y);
			}
			if (y < buffer[0].length - 1) {
				xs.add(x);
				ys.add(y + 1);
			}

		}
	}

	@Override
	public String toString()
	{
		return String.format("(%4d, %4d), %4d x %4d, Pixels: %d",
			left,
			top,
			getWidth(),
			getHeight(),
			pixels);
	}

}
