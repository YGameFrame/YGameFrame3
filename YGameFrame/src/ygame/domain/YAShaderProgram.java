package ygame.domain;

import ygame.framework.domain.YReadBundle;
import ygame.skeleton.YSkeleton;
import ygame.utils.YShaderUtils;
import android.opengl.GLES20;

/**
 * <b>着色（渲染）程序</b>
 * 
 * <p>
 * <b>概述</b>： 您可以通过复写{@link #onInitialize(int)}以及
 * {@link #applyParams(int, YReadBundle, YSkeleton)}
 * 向着色脚本的变量传递值，而不必过多考虑与开放图形库（OpenGL）交互的细节。
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
public abstract class YAShaderProgram
{
	int iProgramHandle = -1;

	private String strFragmentShaderCode;

	private String strVertexShaderCode;

	/**
	 * @param strVertexShaderSrc
	 *                顶点着色器脚本代码
	 * @param strFragmentShaderSrc
	 *                片元着色器脚本代码
	 */
	public YAShaderProgram(String strVertexShaderSrc,
			String strFragmentShaderSrc)
	{
		this.strFragmentShaderCode = strFragmentShaderSrc;
		this.strVertexShaderCode = strVertexShaderSrc;
	}

	void startRendering()
	{
		if (-1 == iProgramHandle)
			initialize();
		GLES20.glUseProgram(iProgramHandle);
	}

	int initialize()
	{
		System.out.println("初始化");
		// 创建程序，更新程序句柄
		iProgramHandle = YShaderUtils.createProgram(
				strVertexShaderCode, strFragmentShaderCode);
		onInitialize(iProgramHandle);
		return iProgramHandle;
	}

	void endRendering()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	/**
	 * 您可以通过复写该方法利用给定参数“程序句柄”——
	 * iProgramHandle来获取相应的 着色器变量句柄。
	 * 
	 * @param iProgramHandle
	 *                着色程序句柄
	 */
	abstract protected void onInitialize(int iProgramHandle);

	/**
	 * 您可以通过复写该方法向着色脚本的变量传递值</br>
	 * <b>不要忘记在该方法中激活顶点骨架</b>：您需要在该函数中调用 类似
	 * {@link YSkeleton#validate()}的方法。
	 * 
	 * @param iProgramHandle
	 *                着色程序句柄
	 * @param bundle
	 *                可读包裹
	 * @param skeleton
	 *                顶点骨架
	 */
	abstract protected void applyParams(int iProgramHandle,
			YReadBundle bundle, YSkeleton skeleton);

}
