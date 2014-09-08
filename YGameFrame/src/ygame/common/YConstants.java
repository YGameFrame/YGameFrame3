package ygame.common;

public final class YConstants
{
	private YConstants()
	{
	}

	public static enum Orientation
	{
		RIGHT("右"), LEFT("左"), UP("上"), DOWN("下");

		private final String name;

		private Orientation(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}

	}
}
