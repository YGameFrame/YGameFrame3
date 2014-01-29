package ygame.skeleton;

import java.util.Random;

import android.opengl.GLES20;
import ygame.utils.YBufferUtils;

/**
 * <b>顶点骨架</b>
 * 
 * <p>
 * <b>概述</b>： 该对象封装了顶点具有四项基本属性：顶点的坐标（x , y ,
 * z）（浮点型）、顶点的颜色（r , g , b , a）（浮点型）、顶点的法向量（nx , ny ,
 * nz）（浮点型）、顶点的纹理坐标（s , t）（浮点型），以及这些顶点 的索引（短整型）。
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>：TODO
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
public abstract class YSkeleton
{

	private YBuffer aColor;
	private YBuffer aTexCoord;
	private YBuffer aNormal;
	private YBuffer aPosition;

	private YFormat format;

	private int iVertexNum;

	private YBuffer bufferIndex;

	private int aColorHandle = -1;
	private int aTexCoordHandle = -1;
	private int aNormalHandle = -1;
	private int aPositionHandle = -1;
	private int iIndexHandle = -1;

	/**
	 * 设置顶点位置坐标
	 * 
	 * @param f_arrPosition
	 *                顶点位置坐标数组
	 */
	final protected void setPositions(float[] f_arrPosition)
	{
		aPosition = new YBuffer(format, f_arrPosition);
	}

	/**
	 * 设置顶点法向量
	 * 
	 * @param f_arrNormals
	 *                顶点法向量数组
	 */
	final protected void setNormals(float[] f_arrNormals)
	{
		aNormal = new YBuffer(format, f_arrNormals);
	}

	/**
	 * 设置顶点纹理坐标
	 * 
	 * @param f_arrTexCoords
	 *                顶点纹理坐标数组
	 */
	final protected void setTexCoords(float[] f_arrTexCoords)
	{
		aTexCoord = new YBuffer(format, f_arrTexCoords);
	}

	/**
	 * 设置顶点颜色
	 * 
	 * @param f_arrColors
	 *                顶点颜色数组
	 */
	final protected void setColors(float[] f_arrColors)
	{
		aColor = new YBuffer(format, f_arrColors);
	}

	/**
	 * 设置顶点的索引
	 * 
	 * @param s_arrIndices
	 *                索引数组
	 */
	final protected void setIndices(short[] s_arrIndices)
	{
		bufferIndex = new YBuffer(format, s_arrIndices);
		iVertexNum = s_arrIndices.length;
	}

	/**
	 * 获取顶点颜色数据
	 * 
	 * @return 顶点颜色数据
	 */
	final public YBuffer getColorData()
	{
		return aColor;
	}

	/**
	 * 获取顶点纹理坐标数据
	 * 
	 * @return 顶点纹理坐标数据
	 */
	final public YBuffer getTexCoordData()
	{
		return aTexCoord;
	}

	/**
	 * 获取顶点法向量数据
	 * 
	 * @return 顶点法向量数据
	 */
	final public YBuffer getNormalData()
	{
		return aNormal;
	}

	/**
	 * 获取顶点位置坐标数据
	 * 
	 * @return 顶点位置坐标数据
	 */
	final public YBuffer getPositionData()
	{
		return aPosition;
	}

	/**
	 * 获取顶点索引数据
	 * 
	 * @return 顶点索引数据
	 */
	final public YBuffer getIndexData()
	{
		return bufferIndex;
	}

	/**
	 * 获取顶点数目
	 * 
	 * @return 顶点数目
	 */
	final public int getVertexNum()
	{
		return iVertexNum;
	}

	/**
	 * 获取顶点缓冲对象颜色句柄
	 * 
	 * @return 顶点颜色句柄
	 */
	final public int getColorHandle()
	{
		if (-1 == aColorHandle)
			aColorHandle = YBufferUtils.upload(aColor.buffer,
					GLES20.GL_ARRAY_BUFFER, 4,
					GLES20.GL_STATIC_DRAW);
		return aColorHandle;
	}

	/**
	 * 获取顶点缓冲对象纹理坐标句柄
	 * 
	 * @return 顶点纹理坐标句柄
	 */
	final public int getTexCoordHandle()
	{
		if (-1 == aTexCoordHandle)
			aTexCoordHandle = YBufferUtils.upload(aTexCoord.buffer,
					GLES20.GL_ARRAY_BUFFER, 4,
					GLES20.GL_STATIC_DRAW);
		return aTexCoordHandle;
	}

	/**
	 * 获取顶点缓冲对象法向量句柄
	 * 
	 * @return 顶点法向量句柄
	 */
	final public int getNormalHandle()
	{
		if (-1 == aNormalHandle)
			aNormalHandle = YBufferUtils.upload(aNormal.buffer,
					GLES20.GL_ARRAY_BUFFER, 4,
					GLES20.GL_STATIC_DRAW);
		return aNormalHandle;
	}

	/**
	 * 获取顶点缓冲对象位置坐标句柄
	 * 
	 * @return 顶点位置坐标句柄
	 */
	final public int getPositionHandle()
	{
		if (-1 == aPositionHandle)
			aPositionHandle = YBufferUtils.upload(aPosition.buffer,
					GLES20.GL_ARRAY_BUFFER, 4,
					GLES20.GL_STATIC_DRAW);
		return aPositionHandle;
	}

	/**
	 * 获取索引缓冲对象句柄
	 * 
	 * @return 索引缓冲对象句柄
	 */
	final public int getIndexHandle()
	{
		if (-1 == iIndexHandle)
			iIndexHandle = YBufferUtils.upload(bufferIndex.buffer,
					GLES20.GL_ELEMENT_ARRAY_BUFFER, 2,
					GLES20.GL_STATIC_DRAW);
		return iIndexHandle;
	}

	private static Random random = new Random();

	/**
	 * 随机产生颜色数据，格式RGBA
	 * 
	 * @param iVertexNum
	 *                顶点个数
	 * @return 颜色数组
	 */
	public static float[] createRandomColorData(int iVertexNum)
	{
		float[] f_arrRes = new float[iVertexNum * 4];
		for (int i = 0; i < f_arrRes.length; i++)
			f_arrRes[i] = random.nextFloat();
		return f_arrRes;
	}

	/**
	 * 产生指定颜色的数据，格式RGBA
	 * 
	 * @param iVertexNum
	 *                顶点个数
	 * @param f_arrRGBA
	 *                指定颜色数据的规格
	 * @return 颜色数组
	 */
	public static float[] createColorData(int iVertexNum, float[] f_arrRGBA)
	{
		float[] f_arrRes = new float[iVertexNum * 4];
		for (int i = 0; i < f_arrRes.length; i++)
			f_arrRes[i] = f_arrRGBA[i % 4];
		return f_arrRes;
	}

}
