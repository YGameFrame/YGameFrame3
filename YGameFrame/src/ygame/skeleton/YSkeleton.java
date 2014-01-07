package ygame.skeleton;

import java.nio.ShortBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ygame.utils.YBufferUtils;
import android.opengl.GLES20;

/**
 * <b>顶点骨架</b>
 * 
 * <p>
 * <b>概述</b>：
 * 该对象封装了顶点具有四项基本属性：顶点的坐标、顶点的颜色、顶点的法向量、顶点的纹理坐标，以及这些顶点
 * 的索引。
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>：如果顶点尚有其他<b>属性变量</b>
 * {@link YBuffer}，您可以通过
 * {@link #addAttributeValue(YBuffer)}将
 * 新增属性填入框架，绘图时通过{@link #getAttributeValue(String)}
 * 获取该属性，从而启用它。
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
public class YSkeleton
{

	private YBuffer aColor;
	private YBuffer aTexCoord;
	private YBuffer aNormal;
	private YBuffer aPosition;
	
	private YFormat format;

	private YIndexBuffer indexBuffer;

	
	public YSkeleton(float[] f_arrVertex)
	{
		aPosition = new YBuffer(format, f_arrVertex);
	}

	/**
	 * 使用该方法设置顶点法向量，默认其在顶点着色器中的变量名为
	 * {@link #NORMAL_VAL_NAME}
	 * 
	 * @param f_arrNormals
	 *                顶点法向量数组
	 */
	final public void setNormals(float[] f_arrNormals)
	{
		aNormal = new YBuffer(format, f_arrNormals);
	}

	/**
	 * 使用该方法设置顶点纹理坐标，默认其在顶点着色器中的变量名为
	 * {@link #TEX_COORD_VAL_NAME}
	 * 
	 * @param f_arrTexCoords
	 *                顶点纹理坐标数组
	 */
	final public void setTexCoords(float[] f_arrTexCoords)
	{
		aTexCoord = new YBuffer(format, f_arrTexCoords);
	}

	/**
	 * 使用该方法设置顶点颜色，默认其在顶点着色器中的变量名为
	 * {@link #COLOR_VAL_NAME}
	 * 
	 * @param f_arrColors
	 *                顶点颜色数组
	 */
	final public void setColors(float[] f_arrColors)
	{
		aColor = new YBuffer(format, f_arrColors);
	}

	/**
	 * 设置顶点的索引
	 * 
	 * @param s_arrIndices
	 *                索引数组
	 */
	final public void setIndices(short[] s_arrIndices)
	{
		indexBuffer = new YIndexBuffer();
		indexBuffer.setData(s_arrIndices);
	}

	private static class YIndexBuffer
	{
		private int iHandle = -1;
		private ShortBuffer buffer;
		private int iElementNum;

		private void confirmHandle()
		{// 上述本地堆，获得显卡中的句柄
			if (-1 == iHandle)
				iHandle = YBufferUtils.upload(buffer,
						GLES20.GL_ELEMENT_ARRAY_BUFFER,
						2, GLES20.GL_STATIC_DRAW);
		}

		private void draw(int iCount, int iStart)
		{
			confirmHandle();
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,
					iHandle);
			GLES20.glDrawElements(GLES20.GL_LINE_LOOP, iCount,
					GLES20.GL_UNSIGNED_SHORT, iStart);
		}

		private void draw()
		{
			draw(iElementNum, 0);
		}

		private void setData(short[] s_arrData)
		{// 将数组存于本地堆
			buffer = YBufferUtils
					.transportArrayToNativeBuffer(s_arrData);
			iElementNum = s_arrData.length;
		}
	}
}
