package mirrg.gubot.noelle.glyph;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.function.Function;

public class SyntaxSerial<T> implements ISyntax<T>
{

	private Function<Hashtable<String, Object>, T> mapper;
	private ArrayList<ISyntax<?>> syntaxes = new ArrayList<>();

	public SyntaxSerial(Function<Hashtable<String, Object>, T> mapper)
	{
		this.mapper = mapper;
	}

	@Override
	public Result<T> match(BufferedImage image, int x, int x2, int y, boolean isFixed)
	{
		Hashtable<String, Object> hash = new Hashtable<>();
		double distanceSq = 0;

		for (ISyntax<?> syntax : syntaxes) {
			Result<?> result = syntax.match(image, x, x2, y, isFixed);

			if (result == null) {
				return null;
			} else {

				if (syntax.getName() != null) hash.put(syntax.getName(), result.value);

				x = result.x;
				x2 = result.x2;
				y = result.y;
				isFixed = isFixed && result.isFixed;
				distanceSq += result.distanceSq;

			}
		}

		return new Result<>(mapper.apply(hash), x, x2, y, isFixed, distanceSq);
	}

	public SyntaxSerial<T> and(ISyntax<?> syntax)
	{
		syntaxes.add(syntax);
		return this;
	}

}
