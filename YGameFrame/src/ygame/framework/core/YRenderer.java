package ygame.framework.core;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

final class YRenderer implements Renderer
{

	protected final YSystem SYSTEM;

	public YRenderer(YSystem system)
	{
		this.SYSTEM = system;
	}

	@SuppressLint("WrongCall")
	@Override
	public void onDrawFrame(GL10 gl10)
	{
		SYSTEM.notifyGL_Draw();
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height)
	{
		System.out.println("表面改变");
		SYSTEM.notifyGL_Ready(gl10, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig arg1)
	{
		System.out.println("表面创建");
		// Set the background clear color to black.
		GLES20.glClearColor(1f, 1f, 0.8f, 1f);
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}

}
