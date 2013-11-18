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
 * {@link YAttributeValue}，您可以通过
 * {@link #addAttributeValue(YAttributeValue)}将
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
	/** <b>顶点的颜色属性在着色器中的默认变量名</b>：aColor */
	public static final String COLOR_VAL_NAME = "aColor";
	/** <b>顶点的纹理坐标属性在着色器中的默认变量名</b>：aTexCoord */
	public static final String TEX_COORD_VAL_NAME = "aTexCoord";
	/** <b>顶点的法向量属性在着色器中的默认变量名</b>：aNormal */
	public static final String NORMAL_VAL_NAME = "aNormal";
	/** <b>顶点的位置属性在着色器中的默认变量名</b>：aPosition */
	public static final String VERTEX_COORD_VAL_NAME = "aPosition";

	private YAttributeValue aColor;
	private YAttributeValue aTexCoord;
	private YAttributeValue aNormal;
	private YAttributeValue aPosition;

	private Map<String, YAttributeValue> mapAttr = new HashMap<String, YAttributeValue>();

	private YIndexBuffer indexBuffer;
	private int iProgram;

	/**
	 * 设置顶点位置坐标
	 * 
	 * @param f_arrVertex
	 *                顶点坐标数组
	 * @param strName
	 *                顶点坐标在顶点着色器中的变量名
	 */
	final public void setPositions(float[] f_arrVertex, String strName)
	{
		aPosition = new YAttributeValue(strName,
				YShaderValueType.YVEC3, f_arrVertex);
	}

	/**
	 * 使用该方法设置顶点位置坐标，默认其在顶点着色器中的变量名为
	 * {@link #VERTEX_COORD_VAL_NAME}
	 * 
	 * @param f_arrVertex
	 *                顶点坐标数组
	 */
	final public void setPositions(float[] f_arrVertex)
	{
		aPosition = new YAttributeValue(VERTEX_COORD_VAL_NAME,
				YShaderValueType.YVEC3, f_arrVertex);
	}

	/**
	 * 设置顶点法向量
	 * 
	 * @param f_arrNormals
	 *                顶点法向量数组
	 * @param strName
	 *                顶点法向量在顶点着色器中的变量名
	 */
	final public void setNormals(float[] f_arrNormals, String strName)
	{
		aNormal = new YAttributeValue(strName, YShaderValueType.YVEC3,
				f_arrNormals);
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
		aNormal = new YAttributeValue(NORMAL_VAL_NAME,
				YShaderValueType.YVEC3, f_arrNormals);
	}

	/**
	 * 设置顶点纹理坐标
	 * 
	 * @param f_arrTexCoords
	 *                顶点纹理坐标数组
	 * @param strName
	 *                顶点纹理坐标在顶点着色器中的变量名
	 */
	final public void setTexCoords(float[] f_arrTexCoords, String strName)
	{
		aTexCoord = new YAttributeValue(strName,
				YShaderValueType.YVEC2, f_arrTexCoords);
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
		aTexCoord = new YAttributeValue(TEX_COORD_VAL_NAME,
				YShaderValueType.YVEC2, f_arrTexCoords);
	}

	/**
	 * 设置顶点颜色
	 * 
	 * @param f_arrColors
	 *                顶点颜色数组
	 * @param strName
	 *                顶点颜色在顶点着色器中的变量名
	 */
	final public void setColors(float[] f_arrColors, String strName)
	{
		aColor = new YAttributeValue(strName, YShaderValueType.YVEC4,
				f_arrColors);
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
		aColor = new YAttributeValue(COLOR_VAL_NAME,
				YShaderValueType.YVEC4, f_arrColors);
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

	/**
	 * 激活顶点骨架
	 */
	final public void validate()
	{
		if (null != aColor)
			aColor.validate();
		if (null != aNormal)
			aNormal.validate();
		if (null != aTexCoord)
			aTexCoord.validate();
		aPosition.validate();

		indexBuffer.draw();
	}

	/**
	 * 激活顶点颜色
	 * 
	 * @param iStart
	 *                激活片段在数据流中的起始位置
	 */
	final public void validateColors(int iStart)
	{
		aColor.validate(iStart);
	}

	/**
	 * 激活顶点纹理坐标
	 * 
	 * @param iStart
	 *                激活片段在数据流中的起始位置
	 */
	final public void validateTexCoords(int iStart)
	{
		aTexCoord.validate(iStart);
	}

	/**
	 * 激活顶点位置坐标
	 * 
	 * @param iStart
	 *                激活片段在数据流中的起始位置
	 */
	final public void validatePositions(int iStart)
	{
		aPosition.validate(iStart);
	}

	/**
	 * 激活顶点法向量
	 * 
	 * @param iStart
	 *                激活片段在数据流中的起始位置
	 */
	final public void validateNormals(int iStart)
	{
		aNormal.validate(iStart);
	}

	/**
	 * 初始化顶点骨架
	 * 
	 * @param iProgramHandle
	 *                渲染该骨架的程序的句柄
	 */
	final public void initilize(int iProgramHandle)
	{
		this.iProgram = iProgramHandle;
		if (null != aColor)
			aColor.initialize(iProgram);
		if (null != aNormal)
			aNormal.initialize(iProgram);
		if (null != aTexCoord)
			aTexCoord.initialize(iProgram);
		aPosition.initialize(iProgram);

		Collection<YAttributeValue> values = mapAttr.values();
		for (YAttributeValue value : values)
			value.initialize(iProgram);
	}

	/**
	 * 向骨架中添加顶点的<b>属性变量</b>
	 * {@link YAttributeValue}
	 * 
	 * @param attributeValue
	 *                添加的属性变量
	 */
	final public void addAttributeValue(YAttributeValue attributeValue)
	{
		mapAttr.put(attributeValue.NAME, attributeValue);
	}

	/**
	 * 获取<b>属性变量</b>{@link YAttributeValue}
	 * 
	 * @param strValueName
	 *                属性变量在着色器中的变量名
	 * @return 属性变量
	 */
	final public YAttributeValue getAttributeValue(String strValueName)
	{
		return mapAttr.get(strValueName);
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
