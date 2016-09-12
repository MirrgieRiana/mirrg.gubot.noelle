package mirrg.gubot.noelle.script;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;
import static mirrg.helium.compile.oxygen.util.WithColor.*;
import static mirrg.helium.compile.oxygen.util.WithProposal.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import mirrg.gubot.noelle.script.FormulaOperationToBoolean.IFunction;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;
import mirrg.helium.compile.oxygen.util.PanelSyntax;
import mirrg.helium.standard.hydrogen.struct.Struct1;

public class ScriptNoelle
{

	public static final String sampleSrc = "heroine='サロメ'/'ナノ'/'マリシャス'";

	public static void main(String[] args)
	{
		VMNoelle vm = new VMNoelle() {

			@Override
			public Stream<String> getVariableNamesForBoolean()
			{
				return Stream.of("true", "false");
			}

			@Override
			public Stream<String> getVariableNamesForDouble()
			{
				return Stream.of("pi", "e", "baseExp");
			}

			@Override
			public Stream<String> getVariableNamesForString()
			{
				return Stream.of("heroine", "class");
			}

			@Override
			public Object getVariable(String name)
			{
				if (name.equals("true")) return true;
				if (name.equals("false")) return false;
				if (name.equals("pi")) return Math.PI;
				if (name.equals("e")) return Math.E;
				if (name.equals("baseExp")) return 453.0;
				if (name.equals("heroine")) return "ゼッペリン";
				if (name.equals("class")) return "アーチャー";
				return null;
			}

		};

		{
			JFrame frame = new JFrame();

			frame.setLayout(new CardLayout());
			frame.add(new PanelSyntax(getParser(vm),
				"(heroine='アイゼル'/'ゼッペリン'/'アデル')|class='アーチャー'"));

			frame.pack();
			frame.setLocationByPlatform(true);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
		}

		System.out.println(getParser(vm).parse("heroine='ゼルマ'").value.calculate());
		System.out.println(getParser(vm).parse("heroine='ゼッペリン'").value.calculate());
		System.out.println(getParser(vm).parse("baseExp>400").value.calculate());
		System.out.println(getParser(vm).parse("baseExp>400&heroine='ゼッペリン'").value.calculate());
		System.out.println(getParser(vm).parse("baseExp<400&heroine='ゼッペリン'").value.calculate());
		System.out.println(getParser(vm).parse("baseExp<400|heroine='ゼッペリン'").value.calculate());
	}

	public static Syntax<IFormulaBoolean> getParser(VMNoelle vm)
	{
		Syntax<IFormulaBoolean> syntaxComparationDouble = wrap(serial(FormulaOperationToBoolean<String, Double>::new)
			.and(withProposal(withColor(regex("[a-zA-Z_0-9]+"), s -> Color.decode("#000088")),
				s -> vm.getVariableNamesForDouble()), (t, t2) -> t.setLeft(t2))
			.and(or((IFunction<String, Double>) null)
				.or(map(string(">="), s -> (a, b) -> (Double) vm.getVariable(a) >= b))
				.or(map(string("<="), s -> (a, b) -> (Double) vm.getVariable(a) <= b))
				.or(map(string(">"), s -> (a, b) -> (Double) vm.getVariable(a) > b))
				.or(map(string("<"), s -> (a, b) -> (Double) vm.getVariable(a) < b))
				.or(map(string("=="), s -> (a, b) -> (Double) vm.getVariable(a) == b))
				.or(map(string("="), s -> (a, b) -> (Double) vm.getVariable(a) == b))
				.or(map(string("!="), s -> (a, b) -> (Double) vm.getVariable(a) != b)),
				(t, t2) -> t.function = t2)
			.and(withColor(regex("[0-9]+(\\.[0-9]+)?"), s -> Color.decode("#ff0000")),
				(t, t2) -> t.setRight(Double.parseDouble(t2))));

		Syntax<String> syntaxString = wrap(map(serial(Struct1<String>::new)
			.and(string("'"))
			.and(withColor(regex("[^']*"), s -> Color.decode("#ff0000")), (t, t2) -> t.setX(t2))
			.and(string("'")),
			Struct1::getX));

		Syntax<IFormulaBoolean> syntaxComparationString = wrap(serial(FormulaOperationToBoolean<String, ArrayList<String>>::new)
			.and(withProposal(withColor(regex("[a-zA-Z_0-9]+"), s -> Color.decode("#880000")),
				s -> vm.getVariableNamesForString()), (t, t2) -> t.setLeft(t2))
			.and(or((IFunction<String, ArrayList<String>>) null)
				.or(map(string("=="), s -> (a, b) -> b.stream().anyMatch(c -> ((String) vm.getVariable(a)).equals(c))))
				.or(map(string("="), s -> (a, b) -> b.stream().anyMatch(c -> ((String) vm.getVariable(a)).equals(c)))),
				(t, t2) -> t.function = t2)
			.and(serial(ArrayList<String>::new)
				.and(syntaxString, (t, t2) -> t.add(t2))
				.and(repeat(serial(Struct1<String>::new)
					.and(string("/"))
					.and(syntaxString, Struct1<String>::setX)),
					(t, t2) -> t2.forEach(a -> t.add(a.getX()))),
				(t, t2) -> t.setRight(t2)));

		Syntax<IFormulaBoolean> syntaxBoolean = map(withProposal(withColor(regex("[a-zA-Z_0-9]+"), s -> Color.decode("#666600")),
			s -> vm.getVariableNamesForBoolean()),
			s -> () -> (Boolean) vm.getVariable(s));

		SyntaxSlot<IFormulaBoolean> syntaxExpression = slot();
		Syntax<IFormulaBoolean> syntaxBrackets = wrap(map(serial(Struct1<IFormulaBoolean>::new)
			.and(string("("))
			.and(syntaxExpression, Struct1::setX)
			.and(string(")")),
			Struct1::getX));

		SyntaxSlot<IFormulaBoolean> syntaxFactor = slot();
		Syntax<IFormulaBoolean> syntaxNot = map(serial(Struct1<IFormulaBoolean>::new)
			.and(string("!"))
			.and(map(syntaxFactor, s -> (IFormulaBoolean) () -> !s.calculate()), Struct1::setX),
			Struct1::getX);

		syntaxFactor.syntax = or((IFormulaBoolean) null)
			.or(syntaxComparationDouble)
			.or(syntaxComparationString)
			.or(syntaxBoolean)
			.or(syntaxBrackets)
			.or(syntaxNot);
		Syntax<IFormulaBoolean> syntaxAnd = FormulaOperationArray.operation(
			syntaxFactor,
			map(string("&"), s -> (a, b) -> a && b));
		Syntax<IFormulaBoolean> syntaxOr = FormulaOperationArray.operation(
			syntaxAnd,
			map(string("|"), s -> (a, b) -> a || b));
		syntaxExpression.setSyntax(syntaxOr);
		return syntaxExpression;
	}

}
