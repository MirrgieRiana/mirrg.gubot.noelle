package mirrg.gubot.noelle;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public interface IConvertable
{

	public default void beforeMarshal()
	{

	}

	public default void marshal(Runnable defaultAction, Object source, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		defaultAction.run();
	}

	public default void afterUnmarshal()
	{

	}

}
