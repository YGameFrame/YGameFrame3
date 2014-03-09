package ygame.texture;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * <b>纹理</b>
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
public class YTexture
{
	private Bitmap bitmap;
	private int iTexHandle = -1;

	/** <b>纹理宽度</b>：以像素为单位 */
	public final int WIDTH;
	/** <b>纹理高度</b>：以像素为单位 */
	public final int HEIGHT;

	private int iSampleType = GLES20.GL_NEAREST;

	/**
	 * 新建一个以指定位图为内容纹理，默认抽样类型{@link GLES20#GL_NEAREST}
	 * 
	 * @param bitmap
	 *                位图
	 */
	public YTexture(Bitmap bitmap)
	{
		this.bitmap = bitmap;
		this.WIDTH = bitmap.getWidth();
		this.HEIGHT = bitmap.getHeight();
	}

	/**
	 * 新建一个以指定位图为内容纹理
	 * 
	 * @param bitmap
	 *                位图
	 * @param iSampleType
	 *                抽样类型——{@link GLES20#GL_LINEAR}或者
	 *                {@link GLES20#GL_NEAREST}
	 */
	public YTexture(Bitmap bitmap, int iSampleType)
	{
		this.bitmap = bitmap;
		this.WIDTH = bitmap.getWidth();
		this.HEIGHT = bitmap.getHeight();
		this.iSampleType = iSampleType;
	}

	/**
	 * 新建一个指定宽高的空纹理，默认抽样类型{@link GLES20#GL_NEAREST}
	 * 
	 * @param iWidth
	 *                高度
	 * @param iHeight
	 *                宽度
	 */
	public YTexture(int iWidth, int iHeight)
	{
		this.WIDTH = iWidth;
		this.HEIGHT = iHeight;
	}

	/**
	 * 新建一个指定宽高的空纹理
	 * 
	 * @param iWidth
	 *                高度
	 * @param iHeight
	 *                宽度
	 * @param iSampleType
	 *                抽样类型——{@link GLES20#GL_LINEAR}或者
	 *                {@link GLES20#GL_NEAREST}
	 */
	public YTexture(int iWidth, int iHeight, int iSampleType)
	{
		this.WIDTH = iWidth;
		this.HEIGHT = iHeight;
		this.iSampleType = iSampleType;
	}

	/**
	 * 获取纹理句柄
	 * 
	 * @return 句柄
	 */
	public int getHandle()
	{
		if (-1 != iTexHandle)
			return iTexHandle;

		// 生成标识
		int[] iTexHandles = new int[1];
		GLES20.glGenTextures(1, iTexHandles, 0);
		iTexHandle = iTexHandles[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iTexHandle);

		if (null != bitmap)
		{// 设置参数
			// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
			// GLES20.GL_TEXTURE_MIN_FILTER,
			// GLES20.GL_NEAREST);
			// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
			// GLES20.GL_TEXTURE_MAG_FILTER,
			// GLES20.GL_LINEAR);
			// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
			// GLES20.GL_TEXTURE_WRAP_S,
			// GLES20.GL_CLAMP_TO_EDGE);
			// GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
			// GLES20.GL_TEXTURE_WRAP_T,
			// GLES20.GL_CLAMP_TO_EDGE);

			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER,
					iSampleType);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER,
					iSampleType);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S,
					GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T,
					GLES20.GL_CLAMP_TO_EDGE);

			// 加载纹理
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, // 纹理类型，在OpenGLES中必须为GL10.GL_TEXTURE_2D
					0, // 纹理的层次，0表示基本图像层，可以理解为直接贴图
					bitmap, // 纹理图像
					0 // 纹理边框尺寸
			);
			// GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0,
			// GLES20.GL_RGBA, bitmap, 0);
			bitmap.recycle();
		} else
		{
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,
					GLES20.GL_RGB, WIDTH, HEIGHT, 0,
					GLES20.GL_RGB,
					GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S,
					GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T,
					GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER,
					iSampleType);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER,
					iSampleType);
		}
		return iTexHandle;
	}
}
