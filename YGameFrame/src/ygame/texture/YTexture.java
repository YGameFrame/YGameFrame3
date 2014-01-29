package ygame.texture;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
*<b>纹理</b>
*
*<p>
*<b>概述</b>：
*TODO
*
*<p>
*<b>建议</b>：
*TODO
*
*<p>
*<b>详细</b>：
*TODO
*
*<p>
*<b>注</b>：
*TODO
*
*<p>
*<b>例</b>：
*TODO
*
*@author yunzhong
*
*/
public class YTexture
{
	private Bitmap bitmap;
	private int iTexHandle = -1;

	public YTexture(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

	public int getHandle()
	{
		if (-1 != iTexHandle)
			return iTexHandle;

		// 生成标识
		int[] iTexHandles = new int[1];
		GLES20.glGenTextures(1, iTexHandles, 0);
		iTexHandle = iTexHandles[0];
		// 设置参数
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iTexHandle);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
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
		bitmap.recycle();
		return iTexHandle;
	}
}
