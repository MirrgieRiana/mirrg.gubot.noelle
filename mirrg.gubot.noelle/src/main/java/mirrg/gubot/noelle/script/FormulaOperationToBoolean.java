package mirrg.gubot.noelle.script;

public class FormulaOperationToBoolean<A, B> implements IFormulaBoolean
{

	public A left;
	public B right;
	public IFunction<A, B> function;

	@Override
	public boolean calculate()
	{
		return function.calculate(left, right);
	}

	public void setLeft(A left)
	{
		this.left = left;
	}

	public void setRight(B right)
	{
		this.right = right;
	}

	public static interface IFunction<A, B>
	{

		public boolean calculate(A a, B b);

	}

}
