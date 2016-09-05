package mirrg.gubot.noelle.glyph;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.function.Function;

public interface ISyntax<T>
{

	public static class Result<T> implements Comparable<Result<T>>
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

	/**
	 * serial
	 */
	public static <T> SyntaxSerial<T> se(Function<Hashtable<String, Object>, T> mapper)
	{
		return new SyntaxSerial<>(mapper);
	}

	public static class SyntaxSerial<T> implements ISyntax<T>
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

	public static <T> SyntaxOr<T> or(T dummy)
	{
		return new SyntaxOr<>(false);
	}

	/**
	 * or extra
	 * 候補のうち最も距離が短いものを選択
	 */
	public static <T> SyntaxOr<T> orEx(T dummy)
	{
		return new SyntaxOr<>(true);
	}

	public static class SyntaxOr<T> implements ISyntax<T>
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

	/**
	 * optional
	 */
	public static <T> ISyntax<Optional<T>> op(ISyntax<T> syntax)
	{
		ISyntax<ArrayList<T>> syntax2 = re(syntax, 0, 1);
		return ma(syntax2, list -> !list.isEmpty() ? Optional.of(list.get(0)) : Optional.empty());
	}

	/**
	 * map
	 */
	public static <A, B> ISyntax<B> ma(ISyntax<A> syntax, Function<A, B> mapper)
	{
		return (image, x, x2, y, isFixed) -> {
			Result<A> result = syntax.match(image, x, x2, y, isFixed);
			if (result == null) return null;
			return new Result<>(
				mapper.apply(result.value),
				result.x,
				result.x2,
				result.y,
				result.isFixed,
				result.distanceSq);
		};
	}

	/**
	 * name
	 */
	public static <T> ISyntax<T> na(String name, ISyntax<T> syntax)
	{
		return new ISyntax<T>() {

			@Override
			public String getName()
			{
				return name;
			}

			@Override
			public Result<T> match(BufferedImage image, int x, int x2, int y, boolean isFixed)
			{
				return syntax.match(image, x, x2, y, isFixed);
			}

		};
	}

	/**
	 * {@code repeat >= 0}
	 */
	public static <T> ISyntax<ArrayList<T>> re0(ISyntax<T> syntax)
	{
		return re(syntax, 0, Integer.MAX_VALUE);
	}

	/**
	 * {@code repeat >= 1}
	 */
	public static <T> ISyntax<ArrayList<T>> re1(ISyntax<T> syntax)
	{
		return re(syntax, 1, Integer.MAX_VALUE);
	}

	/**
	 * {@code max >= repeat >= min}
	 */
	public static <T> ISyntax<ArrayList<T>> re(ISyntax<T> syntax, int min, int max)
	{
		return (image, x, x2, y, isFixed) -> {
			int count = 0;
			double distanceSq = 0;
			ArrayList<T> list = new ArrayList<>();

			while (true) {
				Result<T> result = syntax.match(image, x, x2, y, isFixed);

				if (result == null) {
					if (count < min) {
						return null;
					} else {
						return new Result<>(list, x, x2, y, isFixed, distanceSq);
					}
				} else {
					count++;

					list.add(result.value);

					x = result.x;
					x2 = result.x2;
					y = result.y;
					isFixed = isFixed && result.isFixed;
					distanceSq += result.distanceSq;

					if (count == max) return new Result<>(list, x, x2, y, isFixed, distanceSq);

				}
			}
		};
	}

	/**
	 * space
	 */
	public static ISyntax<String> sp(int width)
	{
		return sp(width, "");
	}

	/**
	 * space
	 */
	public static ISyntax<String> sp(int width, String caption)
	{
		return (image, x, x2, y, isFixed) -> new Result<>(caption, x, x2 + width, y, isFixed, 0);
	}

	public static ISyntax<String> subPixelize()
	{
		return (image, x, x2, y, isFixed) -> new Result<>("", x, x2, y, false, 0);
	}

	/**
	 * 基点からどれだけ横に離れてよいかを定義する。
	 * 基点からrightまで離れている場合にマッチする。
	 */
	public static ISyntax<String> right(int right)
	{
		return (image, x, x2, y, isFixed) -> x2 - x >= right ? null : new Result<>("", x, x2, y, isFixed, 0);
	}

	/**
	 * break line
	 */
	public static ISyntax<String> br()
	{
		return (image, x, x2, y, isFixed) -> new Result<>("\n", x, x, y + 21, true, 0);
	}

	/**
	 * character
	 */
	public static ISyntax<Glyph> ch(RegistryGlyph registry, String character, EnumGlyphColor... colors)
	{
		return (image, x, x2, y, isFixed) -> {

			for (EnumGlyphColor color : colors) {
				if (isFixed) {
					Glyph glyph = registry.getGlyphSet(character).get(color, 0);

					BufferedImage image2 = image.getSubimage(x2 - 1, y - 1, glyph.image.getWidth(), glyph.image.getHeight());
					double distanceSq = glyph.getDistanceSq(image2, isFixed);
					if (distanceSq < registry.distanceLimit) {
						return new Result<>(glyph, x, x2 + glyph.getTrueWidth(), y, isFixed && glyph.isFixed, distanceSq);
					}

				} else {
					for (int i = 0; i < registry.div; i++) {
						Glyph glyph = registry.getGlyphSet(character).get(color, i);

						BufferedImage image2 = image.getSubimage(x2 - 1, y - 1, glyph.image.getWidth(), glyph.image.getHeight());
						double distanceSq = glyph.getDistanceSq(image2, isFixed);
						if (distanceSq < registry.distanceLimit) {
							return new Result<>(glyph, x, x2 + glyph.getTrueWidth(), y, isFixed && glyph.isFixed, distanceSq);
						}

					}
				}
			}

			return null;
		};
	}

	/**
	 * colors
	 */
	public static EnumGlyphColor[] co(EnumGlyphColor... colors)
	{
		return colors;
	}

	/**
	 * @return
	 * 		null: マッチしない, nonnull: 最初にマッチしたもの
	 */
	public Result<T> match(BufferedImage image, int x, int x2, int y, boolean isFixed);

	public default Result<T> match(BufferedImage image, int x, int y)
	{
		return match(image, x, x, y, true);
	}

	public default String getName()
	{
		return null;
	}

}
