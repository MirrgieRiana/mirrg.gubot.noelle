package mirrg.gubot.noelle.glyph;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SyntaxOr<T> implements ISyntax<T>
{

	private boolean isExtra;
	private ArrayList<ISyntax<? extends T>> syntaxes = new ArrayList<>();

	public SyntaxOr(boolean isExtra)
	{
		this.isExtra = isExtra;
	}

	@Override
	public Result<T> match(BufferedImage image, int x, int x2, int y, boolean isFixed)
	{
		if (isExtra) {
			ArrayList<Result<T>> list = new ArrayList<>();

			for (ISyntax<? extends T> syntax : syntaxes) {
				Result<? extends T> result = syntax.match(image, x, x2, y, isFixed);
				if (result != null) {
					list.add(new Result<>(
						result.value,
						result.x,
						result.x2,
						result.y,
						result.isFixed,
						result.distanceSq));
				}
			}

			return list.stream()
				.sorted()
				.findFirst()
				.orElse(null);
		} else {

			for (ISyntax<? extends T> syntax : syntaxes) {
				Result<? extends T> result = syntax.match(image, x, x2, y, isFixed);
				if (result != null) {
					return new Result<>(
						result.value,
						result.x,
						result.x2,
						result.y,
						result.isFixed,
						result.distanceSq);
				}
			}

			return null;
		}
	}

	public SyntaxOr<T> or(ISyntax<? extends T> syntax)
	{
		syntaxes.add(syntax);
		return this;
	}

}
