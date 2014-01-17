package ygame.skeleton;

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

}
