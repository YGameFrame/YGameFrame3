package ygame.skeleton;

import java.nio.Buffer;

import android.opengl.GLES20;

import ygame.program.YAttributeType;
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
public final class YAttributeDataSource
{
	private int handle = -1;
	/** <b>数据源的缓冲</b> */
	public final Buffer buffer;
	/** <b>数据源所对应的变量类型{@link YAttributeType}</b> */
	public final YAttributeType type;

	YAttributeDataSource(float[] f_arrData, YAttributeType type)
	{
		this.buffer = YBufferUtils
				.transportArrayToNativeBuffer(f_arrData);
		this.type = type;
	}

	public YAttributeDataSource(int[] i_arrData, YAttributeType type)
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
	public int getHandle()
	{
		if (!GLES20.glIsBuffer(handle))
			handle = YBufferUtils.upload(buffer,
					GLES20.GL_ARRAY_BUFFER,
					type.bytesPerComponent,
					GLES20.GL_STATIC_DRAW);
		return handle;
	}

}
