package ygame.skeleton.primitives;

import ygame.skeleton.YSkeleton;

/**
 * <b>正方形骨架</b>
 * 
 * <p>
 * <b>概述</b>： TODO
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
public class YSquare extends YSkeleton
{
	private float fSideLength;

	public YSquare(float fSideLength)
	{
		this.fSideLength = fSideLength;
		fillData();
	}

	private void fillData()
	{
		float[] fVertexPosition =
		{ -fSideLength, -fSideLength, 0, // 左下
				-fSideLength, fSideLength, 0,// 左上
				fSideLength, fSideLength, 0, // 右上
				fSideLength, -fSideLength, 0 // 右下
		};

		// rgba
		float[] fVertexColor =
		{ 1, 0, 0, 1, // 红
				0, 1, 0, 1, // 绿
				0, 0, 1, 1,// 蓝
				1, 1, 1, 1 // 白色
		};

		short[] sIndices =
		{ 2, 1, 0, 2, 0, 3 };

		setColors(fVertexColor);
		setPositions(fVertexPosition);
		setIndices(sIndices);

	}
}
