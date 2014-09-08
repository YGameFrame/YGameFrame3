package ygame.extension.primitives;

/**
 * <b>关于y轴对称的正方形</b>
 * 
 * <p>
 * <b>概述</b>： 参见{@link YMirrorYRectangle}
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>： TODO
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public final class YMirrorYSquare extends YMirrorYRectangle
{

	public YMirrorYSquare(float fSideLength, boolean bCreateColor,
			boolean bCreateTexCoord)
	{
		super(fSideLength, fSideLength, bCreateColor, bCreateTexCoord);
	}
}
