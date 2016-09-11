package mirrg.gubot.noelle.script;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.util.ArrayList;

import mirrg.helium.compile.oxygen.parser.core.ISyntax;
import mirrg.helium.standard.hydrogen.struct.Struct2;

public class FormulaOperationArray implements IFormulaBoolean
{

	public IFormulaBoolean left;
	public ArrayList<Struct2<IFunctionBoolean, IFormulaBoolean>> right;

	@Override
	public boolean calculate(VMNoelle vm)
	{
		boolean value = left.calculate(vm);
		for (Struct2<IFunctionBoolean, IFormulaBoolean> a : right) {
			value = a.x.apply(vm, value, a.y.calculate(vm));
		}
		return value;
	}

	public static ISyntax<IFormulaBoolean> operation(
		ISyntax<IFormulaBoolean> syntaxOperand,
		ISyntax<IFunctionBoolean> syntaxOperator)
	{
		return wrap(serial(FormulaOperationArray::new)
			.and(syntaxOperand, (n1, n2) -> n1.left = n2)
			.and(repeat(serial(Struct2<IFunctionBoolean, IFormulaBoolean>::new)
				.and(syntaxOperator, (n1, n2) -> n1.x = n2)
				.and(syntaxOperand, (n1, n2) -> n1.y = n2)),
				(n1, n2) -> n1.right = n2));
	}

}
