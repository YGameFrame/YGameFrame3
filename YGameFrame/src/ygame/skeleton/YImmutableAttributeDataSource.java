package ygame.skeleton;

import java.nio.Buffer;

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
final class YImmutableAttributeDataSource implements YIAttributeDataSource
{
	private int handle = -1;
	/** <b>数据源的缓冲</b> */
	private final Buffer buffer;
	/** <b>数据源所对应的变量类型{@link YAttributeType}</b> */
	private final YAttributeType type;

	YImmutableAttributeDataSource(float[] f_arrData, YAttributeType type)
	{
		this.buffer = YBufferUtils
				.transportArrayToNativeBuffer(f_arrData);
		this.type = type;
	}

	YImmutableAttributeDataSource(int[] i_arrData, YAttributeType type)
	{
		this.buffer = YBufferUtils
				.transportArrayToNativeBuffer(i_arrData);
		this.type = type;
	}

	// public YAttributeDataSource(short[]
	// s_arrData, YAttributeType type)
	// {
	// this.buffer = YBufferUtils
	// .transportArrayToNativeBuffer(s_arrData);
	// this.type = type;
	// }

	/**
	 * 将数据源上传至显存（顶点缓冲对象），并返回其句柄
	 * 
	 * @return 数据源显存中的句柄
	 */
	private int getHandle()
	{
		if (!GLES20.glIsBuffer(handle))
			handle = YBufferUtils.upload(buffer,
					GLES20.GL_ARRAY_BUFFER,
					type.bytesPerComponent,
					GLES20.GL_STATIC_DRAW);
		return handle;
	}

	@Override
	public void bindToAttributeValue(YAttributeValue attribute)
	{
		if (attribute.type != type)
			throw new YException("脚本变量与数据源类型不匹配",
					YImmutableAttributeDataSource.class.getName(),
					"脚本变量类型为：" + attribute.type.name
							+ "，但数据源类型为："
							+ type.name);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, getHandle());

		GLES20.glEnableVertexAttribArray(attribute.valueHandle);
		GLES20.glVertexAttribPointer(attribute.valueHandle,
				attribute.type.componentNum,
				attribute.type.componentType, false, 0, 0);
	}

}
