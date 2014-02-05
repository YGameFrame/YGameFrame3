package ygame.framebuffer;

import ygame.exception.YException;
import ygame.framework.core.YSystem;
import ygame.texture.YTexture;
import android.opengl.GLES20;

/**
 * <b>帧缓冲</b>
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
public class YFrameBuffer
{
	private int iHandle = -1;
	private YTexture texture;

	private int iWidth;
	private int iHeight;

	/**
	 * @param iWidth
	 *                帧缓冲宽度
	 * @param iHeight
	 *                帧缓冲高度
	 */
	public YFrameBuffer(int iWidth, int iHeight)
	{
		this.iWidth = iWidth;
		this.iHeight = iHeight;
		texture = new YTexture(iWidth, iHeight);
	}

	/**
	 * 当您将帧缓冲的内容渲染到屏幕后，应该调用此方法将其清理干净
	 * 
	 * @param system
	 *                系统
	 */
	public void clear(YSystem system)
	{
		begin();
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
				| GLES20.GL_DEPTH_BUFFER_BIT);
		end(system);
	}

	/**
	 * 指示开始将内容离屏渲染
	 */
	public void begin()
	{
		if (-1 == iHandle)
			initialize();
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, iHandle);
		GLES20.glViewport(0, 0, iWidth, iHeight);
		GLES20.glClearColor(0, .5f, .3f, 1);
	}

	/**
	 * 指示结束离屏渲染
	 * 
	 * @param system
	 *                系统
	 */
	public void end(YSystem system)
	{
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glViewport(0, 0, system.YVIEW.getWidth(),
				system.YVIEW.getHeight());
		GLES20.glClearColor(1f, 1f, 0.8f, 1f);
	}

	/**
	 * 获取帧缓冲的内容
	 * 
	 * @return 纹理
	 */
	public YTexture getTexture()
	{
		return texture;
	}

	private void initialize()
	{
		if (-1 != iHandle)
			return;

		// 1.创建深度缓冲
		int[] depthRenderbuffer = new int[1];
		GLES20.glGenRenderbuffers(1, depthRenderbuffer, 0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,
				depthRenderbuffer[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
				GLES20.GL_DEPTH_COMPONENT16, iWidth, iHeight);

		// 2.纹理

		// 3.创建帧缓冲，并挂载上二者
		int[] framebuffer = new int[1];
		GLES20.glGenFramebuffers(1, framebuffer, 0);
		iHandle = framebuffer[0];
		// bind the framebuffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, iHandle);
		// specify texture as color
		// attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0,
				GLES20.GL_TEXTURE_2D, texture.getHandle(), 0);
		// specify depth_renderbufer as depth
		// attachment
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_DEPTH_ATTACHMENT,
				GLES20.GL_RENDERBUFFER, depthRenderbuffer[0]);

		// check for framebuffer complete
		int status = GLES20
				.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (GLES20.GL_FRAMEBUFFER_COMPLETE != status)
			throw new YException("帧缓冲不完全",
					YFrameBuffer.class.getName(), "错误代号："
							+ status);
	}
}
