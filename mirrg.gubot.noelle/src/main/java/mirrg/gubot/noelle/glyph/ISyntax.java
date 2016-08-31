package mirrg.gubot.noelle.glyph;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import mirrg.struct.hydrogen.v1_0.Tuple4;

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

	public static Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>> parseNoelle(BufferedImage image, int x, int y)
	{
		Function<EnumGlyphColor[], ISyntax<Glyph>> decimal = color -> orEx((Glyph) null)
			.or(ch("0", color))
			.or(ch("1", color))
			.or(ch("2", color))
			.or(ch("3", color))
			.or(ch("4", color))
			.or(ch("5", color))
			.or(ch("6", color))
			.or(ch("7", color))
			.or(ch("8", color))
			.or(ch("9", color));
		ISyntax<Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>>> syntax = se(hash -> new Tuple4<>(
			(Integer) hash.get("1"),
			(Integer) hash.get("2"),
			(Optional<Double>) hash.get("3"),
			(Optional<Integer>) hash.get("4")))
				.and(ch("領主の獲得可能経験値：", EnumGlyphColor.WHITE))
				.and(na("1", ma(re1(decimal.apply(co(EnumGlyphColor.WHITE, EnumGlyphColor.ORANGE))),
					list -> Integer.parseInt(list.stream()
						.map(g -> g.value)
						.collect(Collectors.joining()), 10))))
				.and(br())
				.and(ch("ヒロインの獲得可能経験値：", EnumGlyphColor.WHITE))
				.and(na("2", ma(re1(decimal.apply(co(EnumGlyphColor.WHITE, EnumGlyphColor.ORANGE))),
					list -> Integer.parseInt(list.stream()
						.map(g -> g.value)
						.collect(Collectors.joining()), 10))))
				.and(br())
				.and(na("3", op(or((Double) null)
					.or(se(hash -> Double.parseDouble(((Glyph) hash.get("1")).value + "." + ((Glyph) hash.get("2")).value))
						.and(na("1", decimal.apply(co(EnumGlyphColor.ORANGE))))
						.and(ch(".", EnumGlyphColor.ORANGE))
						.and(na("2", decimal.apply(co(EnumGlyphColor.ORANGE))))
						.and(ch("倍　経験値ボーナス発生！", EnumGlyphColor.ORANGE))
						.and(br()))
					.or(se(hash -> Double.parseDouble(((Glyph) hash.get("1")).value))
						.and(na("1", decimal.apply(co(EnumGlyphColor.ORANGE))))
						.and(ch("倍　経験値ボーナス発生！", EnumGlyphColor.ORANGE))
						.and(br())))))
				.and(na("4", op(or((Integer) null)
					.or(se(hash -> 1)
						.and(ch("封印石獲得率↑", EnumGlyphColor.PINK))
						.and(br()))
					.or(se(hash -> 2)
						.and(ch("封印石獲得率↑↑", EnumGlyphColor.RED))
						.and(br())))));
		Result<Tuple4<Integer, Integer, Optional<Double>, Optional<Integer>>> result = syntax.match(image, x, y);
		if (result == null) return null;
		return result.value;
	}

	/**
	 * serial
	 */
	public static <T> ISyntaxSerial<T> se(Function<Hashtable<String, Object>, T> mapper)
	{
		return new ISyntaxSerial<T>(mapper);
	}

	public static class ISyntaxSerial<T> implements ISyntax<T>
	{

		private Function<Hashtable<String, Object>, T> mapper;
		private ArrayList<ISyntax<?>> syntaxes = new ArrayList<>();

		public ISyntaxSerial(Function<Hashtable<String, Object>, T> mapper)
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

		public ISyntaxSerial<T> and(ISyntax<?> syntax)
		{
			syntaxes.add(syntax);
			return this;
		}

	}

	public static <T> ISyntaxOr<T> or(T dummy)
	{
		return new ISyntaxOr<T>(false);
	}

	/**
	 * or extra
	 * 候補のうち最も距離が短いものを選択
	 */
	public static <T> ISyntaxOr<T> orEx(T dummy)
	{
		return new ISyntaxOr<T>(true);
	}

	public static class ISyntaxOr<T> implements ISyntax<T>
	{

		private boolean isExtra;
		private ArrayList<ISyntax<? extends T>> syntaxes = new ArrayList<>();

		public ISyntaxOr(boolean isExtra)
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

		public ISyntaxOr<T> or(ISyntax<? extends T> syntax)
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
	 * break line
	 */
	public static ISyntax<String> br()
	{
		return (image, x, x2, y, isFixed) -> new Result<>("\n", x, x, y + 21, true, 0);
	}

	/**
	 * character
	 */
	public static ISyntax<Glyph> ch(String character, EnumGlyphColor... colors)
	{
		return (image, x, x2, y, isFixed) -> {

			for (EnumGlyphColor color : colors) {
				if (isFixed) {
					Glyph glyph = RegistryGlyph.getGlyphSet(character).get(color, 0);

					BufferedImage image2 = image.getSubimage(x2 - 1, y - 1, glyph.image.getWidth(), glyph.image.getHeight());
					double distanceSq = glyph.getDistanceSq(image2, isFixed);
					if (distanceSq < Glyph.getDistanceLimit(isFixed)) {
						return new Result<>(glyph, x, x2 + glyph.getTrueWidth(), y, isFixed && glyph.isFixed, distanceSq);
					}

				} else {
					for (int i = 0; i < GlyphSet.DIV; i++) {
						Glyph glyph = RegistryGlyph.getGlyphSet(character).get(color, i);

						BufferedImage image2 = image.getSubimage(x2 - 1, y - 1, glyph.image.getWidth(), glyph.image.getHeight());
						double distanceSq = glyph.getDistanceSq(image2, isFixed);
						if (distanceSq < Glyph.getDistanceLimit(isFixed)) {
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