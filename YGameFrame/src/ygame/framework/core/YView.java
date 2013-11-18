package ygame.framework.core;

import ygame.framework.YUnsupportGL2_0Exception;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * <b>渲染视图</b>
 * 
 * <p>
 * <b>概述</b>： 渲染三维界面的视图。
 * 
 * <p>
 * <b>建议</b>：TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>：</br> 1.实例化该对象时，若手机不支持嵌入式开放图库（OpenGL_ES2.0）会抛出异常
 * {@link YUnsupportGL2_0Exception}
 * ，若是通过布局文件配置该视图，应该尤其注意这点。
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public final class YView extends GLSurfaceView
{
	private static final String strTAG = "YView";

	/** <b>控制渲染视图{@link YView}的系统</b> */
	public final YSystem SYSTEM;

	/**
	 * @param context
	 *                上下文
	 * @throws YUnsupportGL2_0Exception
	 *                 手机若不支持OpenGL_ES2.0则抛出该异常
	 */
	public YView(Context context) throws YUnsupportGL2_0Exception
	{
		super(context);
		SYSTEM = new YSystem(this);
		init();
	}

	/**
	 * @param context
	 *                上下文
	 * @param attrs
	 *                属性集
	 * @throws YUnsupportGL2_0Exception
	 *                 手机若不支持OpenGL_ES2.0则抛出该异常
	 */
	public YView(Context context, AttributeSet attrs)
			throws YUnsupportGL2_0Exception
	{
		super(context, attrs);
		SYSTEM = new YSystem(this);
		init();
	}

	private void init() throws YUnsupportGL2_0Exception
	{
		// 检查是否支持gl20
		if (!YSystem.isSupportGL2_0(getContext()))
			throw new YUnsupportGL2_0Exception(strTAG);

		// 设置渲染相关
		setEGLContextClientVersion(2);
		setRenderer(new YRenderer(SYSTEM));
		 super.setRenderMode(RENDERMODE_WHEN_DIRTY);
		 
	}

	/**
	 * 该函数无效，外界不可更改渲染模式
	 * 
	 * @param renderMode
	 *                可传任意值，但无法更改渲染模式
	 */
	@Override
	public void setRenderMode(int renderMode)
	{
		// 屏蔽外界设置
		Log.w(strTAG, "渲染模式设置无效");
		super.setRenderMode(RENDERMODE_WHEN_DIRTY);
	}

	// 设置透明 unfinished
	// TODO

}
