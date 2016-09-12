package mirrg.gubot.noelle.script;

import java.util.stream.Stream;

public abstract class VMNoelle
{

	public abstract Object getVariable(String name);

	public abstract Stream<String> getVariableNamesForString();

	public abstract Stream<String> getVariableNamesForDouble();

	public abstract Stream<String> getVariableNamesForBoolean();

}
