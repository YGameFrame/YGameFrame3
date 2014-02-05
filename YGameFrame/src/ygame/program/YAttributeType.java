package ygame.program;

import ygame.exception.YException;
import android.opengl.GLES20;

/**
 * <b>属性变量类型</b>
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
public enum YAttributeType
{
	/** <b>二维浮点向量</b> */
	VEC2("vec2", 2, GLES20.GL_FLOAT), //
	/** <b>三维浮点向量</b> */
	VEC3("vec3", 3, GLES20.GL_FLOAT), //
	/** <b>四维浮点向量</b> */
	VEC4("vec4", 4, GLES20.GL_FLOAT), ;

	/** <b>类型标志名</b> */
	public final String name;
	/** <b>分量数</b> */
	public final int componentNum;
	/** <b>分量类型</b> */
	public final int componentType;
	/** <b>一个分量的字节数</b> */
	public final int bytesPerComponent;

	private YAttributeType(String name, int componentNum, int componentType)
	{
		this.name = name;
		this.componentNum = componentNum;
		this.componentType = componentType;

		switch (componentType)
		{
		case GLES20.GL_FLOAT:
			bytesPerComponent = 4;
			break;

		default:
			throw new YException("变量类型错误",
					YAttributeType.class.getName(), "");
		}
	}

	/**
	 * 整型标识转换为属性变量类型
	 * 
	 * @param iType
	 *                类型的整型标识
	 * @return 变量类型
	 */
	public static YAttributeType intToType(int iType)
	{
		switch (iType)
		{
		case GLES20.GL_FLOAT_VEC2:
			return VEC2;

		case GLES20.GL_FLOAT_VEC3:
			return VEC3;

		case GLES20.GL_FLOAT_VEC4:
			return VEC4;

		default:
			throw new YException("未知类型，不可转换",
					YAttributeType.class.getName(), "无法转换");
		}
	}

}
