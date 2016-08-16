package mirrg.gubot.noelle;

import java.util.ArrayList;

public class Island
{

	public int left;
	public int right;
	public int top;
	public int bottom;

	public int area;

	public Island(int x, int y)
	{
		this.left = x;
		this.right = x;
		this.top = y;
		this.bottom = y;
	}

	public void addX(int x)
	{
		if (x < left) left = x;
		if (x > right) right = x;
	}

	public void addY(int y)
	{
		if (y < top) top = y;
		if (y > bottom) bottom = y;
	}

	public int getWidth()
	{
		return right - left + 1;
	}

	public int getHeight()
	{
		return bottom - top + 1;
	}

	public void extract(int[][] buffer, int x, int y)
	{
		ArrayList<Runnable> runnables = new ArrayList<>();
		runnables.add(() -> deleteIsland(runnables, buffer, x, y));

		for (int i = 0; i < runnables.size(); i++) {
			runnables.get(i).run();
		}

	}

	private void deleteIsland(ArrayList<Runnable> runnables, int[][] buffer, int x, int y)
	{
		if (buffer[x][y] == 1) {

			area++;
			addX(x);
			addY(y);

			buffer[x][y] = 0;

			if (x > 0) {
				runnables.add(() -> deleteIsland(runnables, buffer, x - 1, y));
			}
			if (y > 0) {
				runnables.add(() -> deleteIsland(runnables, buffer, x, y - 1));
			}
			if (x < buffer.length - 1) {
				runnables.add(() -> deleteIsland(runnables, buffer, x + 1, y));
			}
			if (y < buffer[0].length - 1) {
				runnables.add(() -> deleteIsland(runnables, buffer, x, y + 1));
			}

		}
	}
}
