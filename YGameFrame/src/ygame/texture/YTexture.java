package ygame.texture;

import ygame.framework.core.YGL_Configuration;
import ygame.utils.YLog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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
	private int iTexHandle = -1;

	/** <b>纹理宽度</b>：以像素为单位，如果超出显卡限制、该值会被重新计算 */
	private int iWidth;
	/** <b>纹理高度</b>：以像素为单位 ，如果超出显卡限制、该值会被重新计算 */
	private int iHeight;

	/** <b>纹理抽样方式</b> */
	public int iSampleType = GLES20.GL_NEAREST;
	final private int iResId;
	private Resources resources;

	private Bitmap bitmap;

	final private boolean bNoBitmap;
	
	final String id;

	/**
	 * 新建一个以指定位图为内容的纹理，默认抽样类型{@link GLES20#GL_NEAREST}
	 * 
	 * @param iResId
	 *                资源标识
	 * @param resources
	 *                资源
	 */
	public YTexture(int iResId, Resources resources)
	{
		this.iResId = iResId;
		this.resources = resources;
		this.bNoBitmap = false;
		this.id = iResId + "";
		YTextureManager.INSTANCE.add(this);
	}

	/**
	 * 新建一个以指定位图为内容的纹理
	 * 
	 * @param iResId
	 *                资源标识
	 * @param resources
	 *                资源
	 * @param iSampleType
	 *                抽样类型——{@link GLES20#GL_LINEAR}或者
	 *                {@link GLES20#GL_NEAREST}
	 */
	public YTexture(int iResId, Resources resources, int iSampleType)
	{
		this.iResId = iResId;
		this.resources = resources;
		this.iSampleType = iSampleType;
		this.bNoBitmap = false;
		this.id = iResId + "";
		YTextureManager.INSTANCE.add(this);
	}

	/**
	 * 以位图为内容新建纹理<br>
	 * <b>注：</b>如果位图高宽超出显卡限制将会依高宽比例重新计算大小
	 * 
	 * @param bitmap
	 *                内容位图
	 */
	public YTexture(Bitmap bitmap)
	{
		this.bitmap = bitmap;
		this.bNoBitmap = false;
		this.iResId = -1;
		this.id = null;
	}

	/**
	 * 新建一个指定宽高的空纹理，默认抽样类型{@link GLES20#GL_NEAREST}<br>
	 * <b>注：</b>如果高宽超出显卡限制将会依高宽比例重新计算大小
	 * 
	 * @param iWidth
	 *                高度
	 * @param iHeight
	 *                宽度
	 */
	public YTexture(int iWidth, int iHeight)
	{
		this.iWidth = iWidth;
		this.iHeight = iHeight;
		this.bNoBitmap = true;
		this.iResId = -1;
		this.id = null;
	}

	/**
	 * 新建一个指定宽高的空纹理 </br> <b>注：</b>如果高宽超出显卡限制将会依高宽比例重新计算大小
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
		this.iWidth = iWidth;
		this.iHeight = iHeight;
		this.iSampleType = iSampleType;
		this.bNoBitmap = true;
		this.iResId = -1;
		this.id = null;
	}

	/**
	 * 获取纹理句柄
	 * 
	 * @return 句柄
	 */
	public int getHandle()
	{
		if (GLES20.glIsTexture(iTexHandle))
			return iTexHandle;
		
		YTexture textureCache = YTextureManager.INSTANCE.get(id);
		if(null != textureCache && GLES20.glIsTexture(textureCache.iTexHandle))
			return textureCache.iTexHandle;

		// 生成标识
		int[] iTexHandles = new int[1];
		GLES20.glGenTextures(1, iTexHandles, 0);
		iTexHandle = iTexHandles[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iTexHandle);

		if (!bNoBitmap)
		{// 纹理有位图对象作为内容
			// 确认位图对象
			Bitmap bitmapFinal = confirmBitmap();
			// // 记录高宽
			iWidth = bitmapFinal.getWidth();
			iHeight = bitmapFinal.getHeight();
			// 设置参数
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
					bitmapFinal, // 纹理图像
					0 // 纹理边框尺寸
			);
			bitmapFinal.recycle();
		} else
		{
			int[] confirmSize = confirmSize();
			// 记录高宽
			iWidth = confirmSize[0];
			iHeight = confirmSize[1];
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,
					GLES20.GL_RGB, iWidth, iHeight, 0,
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

	/**
	 * 获取纹理宽度，以像素计</br> <b>注：</b>如果纹理尺寸超出显卡限制，该值会被重新计算
	 * 
	 * @return 宽度
	 */
	public int getWidth()
	{
		return iWidth;
	}

	/**
	 * 获取纹理高度，以像素计 <b>注：</b>如果纹理尺寸超出显卡限制，该值会被重新计算
	 * 
	 * @return 高度
	 */
	public int getHeight()
	{
		return iHeight;
	}

	/**
	 * 0宽， *
	 * 
	 * @return 宽高
	 */
	private int[] confirmSize()
	{
		// 定制的空纹理高宽满足要求，不做更改，直接返回
		final float fHeight = iHeight;
		final float fWidth = iWidth;
		final boolean bMaxSideIsWidth = fWidth > fHeight;
		final int iMaxSideLen = (int) (bMaxSideIsWidth ? fWidth
				: fHeight);
		YGL_Configuration configuration = YGL_Configuration
				.getInstanceInGL();
		if (configuration.getMaxTextureSize() >= iMaxSideLen)
			return new int[]
			{ iWidth, iHeight };

		// 尺寸不合要求，更改后返回
		int iOutWidth = 0;
		int iOutHeight = 0;
		if (bMaxSideIsWidth)
		{
			iOutWidth = configuration.getMaxTextureSize();
			iOutHeight = (int) (fHeight / fWidth * configuration
					.getMaxTextureSize());
		} else
		{
			iOutHeight = configuration.getMaxTextureSize();
			iOutWidth = (int) (fWidth / fHeight * configuration
					.getMaxTextureSize());
		}
		YLog.w(getClass().getName(), "纹理位图被重新缩放，原高宽：" + fHeight + "_"
				+ fWidth + "，现高宽：" + iOutHeight + "_"
				+ iOutWidth);
		return new int[]
		{ iOutWidth, iOutHeight, };
	}

	/**
	 * 确认位图尺寸是否超出显卡限制，如若超出则需要缩小
	 * 
	 * @return 确认后的位图
	 */
	private Bitmap confirmBitmap()
	{
		// if (null == bitmap)
		// return decodeBitmap();
		// 1.尚未有位图，需要由资源标识解析出
		Options opts = new Options();
		opts.inScaled = false;
		if (null == bitmap)
			bitmap = BitmapFactory.decodeResource(resources,
					iResId, opts);

		// 2.尺寸合乎要求，不做缩小，直接返回
		final float fHeight = bitmap.getHeight();
		final float fWidth = bitmap.getWidth();
		final boolean bMaxSideIsWidth = fWidth > fHeight;
		final int iMaxSideLen = (int) (bMaxSideIsWidth ? fWidth
				: fHeight);
		YGL_Configuration configuration = YGL_Configuration
				.getInstanceInGL();
		if (configuration.getMaxTextureSize() >= iMaxSideLen)
			return bitmap;

		// 3.尺寸不合要求，缩小后返回
		int iOutWidth = 0;
		int iOutHeight = 0;
		if (bMaxSideIsWidth)
		{
			iOutWidth = configuration.getMaxTextureSize();
			iOutHeight = (int) (fHeight / fWidth * configuration
					.getMaxTextureSize());
		} else
		{
			iOutHeight = configuration.getMaxTextureSize();
			iOutWidth = (int) (fWidth / fHeight * configuration
					.getMaxTextureSize());
		}
		Bitmap bitmapRes = Bitmap.createScaledBitmap(bitmap, iOutWidth,
				iOutHeight, false);
		bitmap.recycle();
		YLog.w(getClass().getName(), "纹理位图被重新缩放，原高宽：" + fHeight + "_"
				+ fWidth + "，现高宽：" + iOutHeight + "_"
				+ iOutWidth);
		return bitmapRes;
	}

	public void destroy()
	{
		if (GLES20.glIsTexture(iTexHandle))
			GLES20.glDeleteTextures(1, new int[]
			{ iTexHandle }, 0);
	}

}
