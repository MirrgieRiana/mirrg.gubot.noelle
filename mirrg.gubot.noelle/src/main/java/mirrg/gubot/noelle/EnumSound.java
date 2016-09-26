package mirrg.gubot.noelle;

import java.io.File;

public enum EnumSound
{
	DIE,
	SHOT,
	;

	public final File file;

	private EnumSound()
	{
		file = new File(name().toLowerCase() + ".mp3");
	}

}
