package ygame.skeleton;

import java.nio.Buffer;

import ygame.utils.YBufferUtils;

/**
 * <b>本地缓冲</b>
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
public final class YBuffer
{
	/** <b>数据源缓冲</b> */
	public final Buffer buffer;
	public final YFormat format;

	/**
	 * @param format
	 *                属性变量的类型
	 *                {@link YFormat}
	 * @param f_arrData
	 *                属性变量的数据源
	 */
	public YBuffer(YFormat format,
			float[] f_arrData)
	{
		this.format = format;
		this.buffer = YBufferUtils
				.transportArrayToNativeBuffer(f_arrData);
	}

	/**
	 * @param format
	 *                属性变量的类型
	 *                {@link YFormat}
	 * @param i_arrData
	 *                属性变量的数据源
	 */
	public YBuffer(YFormat format,
			int[] i_arrData)
	{
		this.format = format;
		this.buffer = YBufferUtils
				.transportArrayToNativeBuffer(i_arrData);
	}

}
