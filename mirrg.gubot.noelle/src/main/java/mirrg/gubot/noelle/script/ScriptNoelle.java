package mirrg.gubot.noelle.script;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import mirrg.gubot.noelle.script.FormulaOperationToBoolean.IFunction;
import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;
import mirrg.helium.compile.oxygen.util.Colored;
import mirrg.helium.compile.oxygen.util.PanelSyntax;
import mirrg.helium.standard.hydrogen.struct.Struct1;

public class ScriptNoelle
{

	public static final String sampleSrc = "heroine='サロメ'/'ナノ'/'マリシャス'";

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();

		frame.setLayout(new CardLayout());
		frame.add(new PanelSyntax(getParser(),
			"(heroine='アイゼル'/'ゼッペリン'/'アデル')|class='アーチャー'"));

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);

		VMNoelle vm = new VMNoelle() {

			@Override
			public Object getVariable(String name)
			{
				if (name.equals("heroine")) return "ゼッペリン";
				if (name.equals("baseExp")) return 453.0;
				return null;
			}

		};

		System.out.println(getParser().parse("heroine='ゼルマ'").value.calculate(vm));
		System.out.println(getParser().parse("heroine='ゼッペリン'").value.calculate(vm));
		System.out.println(getParser().parse("baseExp>400").value.calculate(vm));
		System.out.println(getParser().parse("baseExp>400&heroine='ゼッペリン'").value.calculate(vm));
		System.out.println(getParser().parse("baseExp<400&heroine='ゼッペリン'").value.calculate(vm));
		System.out.println(getParser().parse("baseExp<400|heroine='ゼッペリン'").value.calculate(vm));
	}

	private static ISyntax<IFormulaBoolean> parser;

	public static ISyntax<IFormulaBoolean> getParser()
	{
		if (parser == null) {

			ISyntax<IFormulaBoolean> syntaxComparationDouble = wrap(serial(FormulaOperationToBoolean<String, Double>::new)
				.and(colored(regex("[a-zA-Z_0-9]+"), Color.decode("#000088")), (t, t2) -> t.setLeft(t2))
				.and(or((IFunction<String, Double>) null)
					.or(map(string(">="), s -> (vm, a, b) -> (Double) vm.getVariable(a) >= b))
					.or(map(string("<="), s -> (vm, a, b) -> (Double) vm.getVariable(a) <= b))
					.or(map(string(">"), s -> (vm, a, b) -> (Double) vm.getVariable(a) > b))
					.or(map(string("<"), s -> (vm, a, b) -> (Double) vm.getVariable(a) < b))
					.or(map(string("=="), s -> (vm, a, b) -> (Double) vm.getVariable(a) == b))
					.or(map(string("="), s -> (vm, a, b) -> (Double) vm.getVariable(a) == b))
					.or(map(string("!="), s -> (vm, a, b) -> (Double) vm.getVariable(a) != b)),
					(t, t2) -> t.function = t2)
				.and(colored(regex("[0-9]+(\\.[0-9]+)?"),
					Color.decode("#ff0000")), (t, t2) -> t.setRight(Double.parseDouble(t2))));

			ISyntax<String> syntaxString = wrap(map(serial(Struct1<String>::new)
				.and(string("'"))
				.and(colored(regex("[^']*"), Color.decode("#ff0000")), (t, t2) -> t.setX(t2))
				.and(string("'")),
				Struct1::getX));

			ISyntax<IFormulaBoolean> syntaxComparationString = wrap(serial(FormulaOperationToBoolean<String, ArrayList<String>>::new)
				.and(colored(regex("[a-zA-Z_0-9]+"), Color.decode("#880000")), (t, t2) -> t.setLeft(t2))
				.and(or((IFunction<String, ArrayList<String>>) null)
					.or(map(string("=="), s -> (vm, a, b) -> b.stream().anyMatch(c -> ((String) vm.getVariable(a)).equals(c))))
					.or(map(string("="), s -> (vm, a, b) -> b.stream().anyMatch(c -> ((String) vm.getVariable(a)).equals(c)))),
					(t, t2) -> t.function = t2)
				.and(serial(ArrayList<String>::new)
					.and(syntaxString, (t, t2) -> t.add(t2))
					.and(repeat(serial(Struct1<String>::new)
						.and(string("/"))
						.and(syntaxString, Struct1<String>::setX)),
						(t, t2) -> t2.forEach(a -> t.add(a.getX()))),
					(t, t2) -> t.setRight(t2)));

			ISyntax<IFormulaBoolean> syntaxBoolean = map(colored(regex("[a-zA-Z_0-9]+"), Color.decode("#666600")),
				s -> vm -> (Boolean) vm.getVariable(s));

			SyntaxSlot<IFormulaBoolean> syntaxExpression = slot();
			ISyntax<IFormulaBoolean> syntaxBrackets = wrap(map(serial(Struct1<IFormulaBoolean>::new)
				.and(string("("))
				.and(syntaxExpression, Struct1::setX)
				.and(string(")")),
				Struct1::getX));

			SyntaxSlot<IFormulaBoolean> syntaxFactor = slot();
			ISyntax<IFormulaBoolean> syntaxNot = map(serial(Struct1<IFormulaBoolean>::new)
				.and(string("!"))
				.and(map(syntaxFactor, s -> (IFormulaBoolean) vm -> !s.calculate(vm)), Struct1::setX),
				Struct1::getX);

			syntaxFactor.syntax = or((IFormulaBoolean) null)
				.or(syntaxComparationDouble)
				.or(syntaxComparationString)
				.or(syntaxBoolean)
				.or(syntaxBrackets)
				.or(syntaxNot);
			ISyntax<IFormulaBoolean> syntaxAnd = FormulaOperationArray.operation(
				syntaxFactor,
				map(string("&"), s -> (vm, a, b) -> a && b));
			ISyntax<IFormulaBoolean> syntaxOr = FormulaOperationArray.operation(
				syntaxAnd,
				map(string("|"), s -> (vm, a, b) -> a || b));
			syntaxExpression.setSyntax(syntaxOr);
			parser = syntaxExpression;
		}
		return parser;
	}

	private static <T> ISyntax<T> colored(ISyntax<T> syntax, Color color)
	{
		return pack(map(syntax, s -> new Colored<>(s, color)), Colored<T>::get);
	}

}
