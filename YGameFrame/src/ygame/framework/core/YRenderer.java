package ygame.framework.core;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
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
		// GL初始化
		// TODO
		// GL及其下属GL都已经初始化完毕，调用：
		SYSTEM.notifyGL_Ready(gl10, width, height);
		System.out.println("表面改变");
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig arg1)
	{
		System.out.println("表面创建");
	}

}
