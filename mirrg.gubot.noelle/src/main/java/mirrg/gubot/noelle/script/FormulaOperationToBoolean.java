package mirrg.gubot.noelle.script;

public class FormulaOperationToBoolean<A, B> implements IFormulaBoolean
{

	public A left;
	public B right;
	public IFunction<A, B> function;

	@Override
	public boolean calculate(VMNoelle vm)
	{
		return function.calculate(vm, left, right);
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

		public boolean calculate(VMNoelle vm, A a, B b);

	}

}
