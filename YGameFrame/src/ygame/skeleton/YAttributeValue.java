package ygame.skeleton;

import java.nio.Buffer;

import ygame.exception.GetHandleException;
import ygame.utils.YBufferUtils;
import android.opengl.GLES20;

/**
 * <b>属性变量</b>
 * 
 * <p>
 * <b>概述</b>： 对应于顶点着色器中限定符为“attribute”的变量，
 * 它是顶点的一个属性，如颜色、位置坐标、法向量等。
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
public final class YAttributeValue
{
	private static final String strTAG = "YAttributeValue";
	/** <b>属性变量在着色器中的变量名</b> */
	public final String NAME;
	/** <b>属性变量在着色器中的句柄</b> */
	private int iValueHandle = -1;

	/** <b>数据源的句柄</b> */
	private int iBufferHandle = -1;
	/** <b>数据源缓冲</b> */
	private final Buffer buffer;
	private final YShaderValueType type;

	/**
	 * @param strName
	 *                属性变量在顶点着色器中声明的变量名
	 * @param type
	 *                属性变量的类型
	 *                {@link YShaderValueType}
	 * @param f_arrData
	 *                属性变量的数据源
	 */
	public YAttributeValue(String strName, YShaderValueType type,
			float[] f_arrData)
	{
		this.NAME = strName;
		this.type = type;
		this.buffer = YBufferUtils
				.transportArrayToNativeBuffer(f_arrData);
	}

	/**
	 * @param strName
	 *                属性变量在顶点着色器中声明的变量名
	 * @param type
	 *                属性变量的类型
	 *                {@link YShaderValueType}
	 * @param i_arrData
	 *                属性变量的数据源
	 */
	public YAttributeValue(String strName, YShaderValueType type,
			int[] i_arrData)
	{
		this.NAME = strName;
		this.type = type;
		this.buffer = YBufferUtils
				.transportArrayToNativeBuffer(i_arrData);
	}

	/**
	 * 激活属性变量
	 * 
	 * @param iStart
	 *                激活片段在数据流中的起始位置
	 */
	public void validate(int iStart)
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, iBufferHandle);
		GLES20.glEnableVertexAttribArray(iValueHandle);
		GLES20.glVertexAttribPointer(iValueHandle, type.DIMENSION,
				type.DIMENSION_TYPE, false, 0, iStart);
	}

	/**
	 * 激活整段属性变量
	 */
	public void validate()
	{
		validate(0);
	}

	void initialize(int iProgram)
	{
		iBufferHandle = YBufferUtils
				.upload(buffer, GLES20.GL_ARRAY_BUFFER,
						type.BYTES_PER_DIMENSION,
						GLES20.GL_STATIC_DRAW);
		if (-1 == iBufferHandle)
			throw new GetHandleException(NAME + "属性变量获取缓冲句柄时失败",
					strTAG, null);

		iValueHandle = GLES20.glGetAttribLocation(iProgram, NAME);
		if (-1 == iValueHandle)
			throw new GetHandleException(NAME + "属性变量获取变量句柄时失败",
					strTAG, "可能是属性变量名：NAME与着色脚本变量名不一致");
	}

}
