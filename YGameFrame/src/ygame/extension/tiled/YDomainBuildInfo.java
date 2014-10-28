package ygame.extension.tiled;

public class YDomainBuildInfo
{
	public final String key;
	public final float x, y, z;
	public final float width;
	public final float height;

	public YDomainBuildInfo(String key, float x, float y, float z,
			float width, float height)
	{
		this.key = key;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
	}

}
