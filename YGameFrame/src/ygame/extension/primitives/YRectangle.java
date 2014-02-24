package ygame.extension.primitives;

import ygame.skeleton.YSkeleton;

/**
*<b>矩形</b>
*
*<p>
*<b>概述</b>：
*TODO
*
*<p>
*<b>建议</b>：
*TODO
*
*<p>
*<b>详细</b>：
*TODO
*
*<p>
*<b>注</b>：
*TODO
*
*<p>
*<b>例</b>：
*TODO
*
*@author yunzhong
*
*/
public class YRectangle extends YSkeleton
{
	private boolean bCreateColor;
	private boolean bCreateTexCoord;
	private float fHeight;
	private float fWidth;

	public YRectangle(float fWidth, float fHeight , boolean bCreateColor,
			boolean bCreateTexCoord)
	{
		this.fWidth = fWidth;
		this.fHeight = fHeight;
		this.bCreateColor = bCreateColor;
		this.bCreateTexCoord = bCreateTexCoord;
		fillData();
	}

	private void fillData()
	{
		float fHalfWidth = fWidth / 2;
		float fHalfHeight = fHeight / 2;
		// 顶点位置坐标
		float[] fVertexPosition =
		{ -fHalfWidth, -fHalfHeight, 0, // 左下
				-fHalfWidth, fHalfHeight, 0,// 左上
				fHalfWidth, fHalfHeight, 0, // 右上
				fHalfWidth, -fHalfHeight, 0 // 右下
		};
		setPositions(fVertexPosition);

		// 索引坐标
		short[] sIndices =
		{ 2, 1, 0, 2, 0, 3 };
		setIndices(sIndices);

		// 颜色坐标
		if (bCreateColor)
		{
			float[] fVertexColor = createRandomColorData(4);
			setColors(fVertexColor);
		}

		// 法向量坐标

		// 纹理坐标
		if (bCreateTexCoord)
		{
			// 坐标系镜像
			float[] fTexCoord =
			{ 0, 1, // 左下
					0, 0, // 左上
					1, 0, // 右上
					1, 1, // 右下
			};
			setTexCoords(fTexCoord);
		}

	}
}
