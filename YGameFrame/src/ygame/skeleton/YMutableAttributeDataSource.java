package ygame.skeleton;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import ygame.exception.YException;
import ygame.program.YAttributeType;
import ygame.program.YAttributeValue;
import ygame.utils.YBufferUtils;

/**
 * <b>属性变量数据源</b>
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
final class YMutableAttributeDataSource implements YIAttributeDataSource
{
	/** <b>数据源的缓冲</b> */
	private final Buffer buffer;
	/** <b>数据源所对应的变量类型{@link YAttributeType}</b> */
	private final YAttributeType type;

	YMutableAttributeDataSource(float[] f_arrData, YAttributeType type)
	{
		this.buffer = YBufferUtils
				.transportArrayToNativeBuffer(f_arrData);
		this.type = type;
	}

	YMutableAttributeDataSource(int[] i_arrData, YAttributeType type)
	{
		this.buffer = YBufferUtils
				.transportArrayToNativeBuffer(i_arrData);
		this.type = type;
	}

	void setDatas(float[] datas)
	{
		buffer.clear();
		((FloatBuffer) buffer).put(datas).position(0);
	}

	void setDatas(int[] datas)
	{
		buffer.clear();
		((IntBuffer) buffer).put(datas);
	}

	@Override
	public void bindToAttributeValue(YAttributeValue attribute)
	{
		if (attribute.type != type)
			throw new YException("脚本变量与数据源类型不匹配",
					YMutableAttributeDataSource.class
							.getName(), "脚本变量类型为："
							+ attribute.type.name
							+ "，但数据源类型为："
							+ type.name);

		GLES20.glEnableVertexAttribArray(attribute.valueHandle);
		GLES20.glVertexAttribPointer(attribute.valueHandle,
				attribute.type.componentNum,
				attribute.type.componentType, false, 0, buffer);
	}

}
