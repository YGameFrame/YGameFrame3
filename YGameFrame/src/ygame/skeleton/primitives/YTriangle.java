package ygame.skeleton.primitives;

import ygame.skeleton.YSkeleton;

/**
 * <b>三角形骨架 </b>
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
public class YTriangle extends YSkeleton
{
	public YTriangle()
	{
		float[] fVertexPosition =
		{ -1, 0, 0,// 左点
				1, 0, 0, // 右点
				0, 1, 0 // 上点
		};

		// rgba
		float[] fVertexColor =
		{ 1, 0, 0, 1, // 红
				0, 1, 0, 1, // 绿
				0, 0, 1, 1,// 蓝
		};

		short[] sIndices =
		{ 1, 2, 0 };

		setColors(fVertexColor);
		setPositions(fVertexPosition);
		setIndices(sIndices);
	}
}
