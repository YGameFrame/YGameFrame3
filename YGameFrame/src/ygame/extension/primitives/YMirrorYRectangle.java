package ygame.extension.primitives;

import ygame.extension.primitives.YRectangle;

/**
 * <b>关于y轴对称的矩形</b>
 * 
 * <p>
 * <b>概述</b>： 由于纹理坐标与正常矩形关于y轴对称，所以可以实现图像左右镜面变换，
 * 直接使用该对象效率更高，省去了矩阵变换的计算消耗。
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
public class YMirrorYRectangle extends YRectangle
{

	public YMirrorYRectangle(float fWidth, float fHeight,
			boolean bCreateColor, boolean bCreateTexCoord)
	{
		super(fWidth, fHeight, bCreateColor, bCreateTexCoord);
		// 纹理坐标
		if (bCreateTexCoord)
		{
			// 坐标系镜像
			float[] fTexCoord =
			{ 1, 1, // 左下
					1, 0, // 左上
					0, 0, // 右上
					0, 1, // 右下
			};
			setTexCoords(fTexCoord);
		}
	}
}
