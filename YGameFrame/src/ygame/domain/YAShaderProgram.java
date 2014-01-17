package ygame.domain;

import ygame.framework.domain.YReadBundle;
import ygame.framework.domain.YWriteBundle;
import ygame.skeleton.YSkeleton;
import ygame.utils.YShaderUtils;
import android.opengl.GLES20;

/**
 * <b>着色（渲染）程序</b>
 * 
 * <p>
 * <b>概述</b>： 您可以通过复写 {@link #onInitialize(int)}以及
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
 * @param <DA>
 *                <b>参数适配器</b>
 *                {@link YAParametersAdapter}类型
 */
public abstract class YAShaderProgram<DA extends YAShaderProgram.YAParametersAdapter>
{
	int iProgramHandle = -1;

	private String strFragmentShaderCode;

	private String strVertexShaderCode;

	DA daParamsAdapter;

	/**
	 * @param strVertexShaderSrc
	 *                顶点着色器脚本代码
	 * @param strFragmentShaderSrc
	 *                片元着色器脚本代码
	 * @param clazzDataAdapter
	 *                该渲染程序的参数适配器类
	 */
	public YAShaderProgram(String strVertexShaderSrc,
			String strFragmentShaderSrc, Class<DA> clazzDataAdapter)
	{
		this.strFragmentShaderCode = strFragmentShaderSrc;
		this.strVertexShaderCode = strVertexShaderSrc;
		this.daParamsAdapter = createDataAdapter(clazzDataAdapter);
	}

	void startRendering(YSkeleton skeleton)
	{
		if (-1 == iProgramHandle)
			initialize(skeleton);
		GLES20.glUseProgram(iProgramHandle);
	}

	void initialize(YSkeleton skeleton)
	{
		System.out.println("初始化");
		// 创建程序，更新程序句柄
		iProgramHandle = YShaderUtils.createProgram(
				strVertexShaderCode, strFragmentShaderCode);
		onInitialize(iProgramHandle, skeleton);
	}

	void endRendering()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private DA createDataAdapter(Class<DA> clazzDataAdapter)
	{
		try
		{
			return clazzDataAdapter.newInstance();
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 您可以通过复写该方法利用给定参数“程序句柄”——
	 * iProgramHandle来获取相应的 着色器变量句柄。
	 * 
	 * @param iProgramHandle
	 *                着色程序句柄
	 * @param skeleton
	 *                顶点骨架
	 */
	abstract protected void onInitialize(int iProgramHandle,
			YSkeleton skeleton);

	/**
	 * 您可以通过复写该方法向着色脚本的变量传递值</br>
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

	/**
	 * <b>参数适配器</b>
	 * 
	 * <p>
	 * <b>概述</b>：这是<b>渲染程序</b>的参数适配器。</br>
	 * <li>作为使用者，您应该依次填写适配器中所声明的参数；
	 * <li>如果您开发了一个<b> 渲染程序</b> ，您应该再开发本类
	 * {@link YAParametersAdapter}的一个子类，
	 * 用来向外界声明使用此<b>渲染程序</b>时应该向其传递怎样的参数。
	 * 
	 * <p>
	 * <b>建议</b>： TODO
	 * 
	 * <p>
	 * <b>详细</b>： TODO
	 * 
	 * <p>
	 * <b>注</b>：
	 * 作为使用者，为了确保数据的正确，您在每周期都应该对<b>适配器</b
	 * >中声明的参数<b>全部</b>填写一遍。
	 * 
	 * <p>
	 * <b>例</b>： TODO
	 * 
	 * @author yunzhong
	 * 
	 */
	// 该类的设计目的是向客户提供方便的接口向可写包裹中写入数据。客户不用接触可写包裹，
	// 而直接使用该类——更直观、更易于理解。该类就是一个从客户所写参数到可写包裹的一个映射，
	// 除此而外，不应为该类设计其他用途。
	// 作为渲染程序的设计者，可同时接触到可写包裹和可读包裹，所以自己最清楚每个参数相应的键值，
	// 自己匹配键值即可，从而不用向外界声明这些繁琐的键值。
	public static abstract class YAParametersAdapter
	{
		/**
		 * 将<b>适配器</b>中的属性依次映射进<b>可写包裹</b>
		 * {@link YWriteBundle}
		 * 
		 * @param bundle
		 *                可写包裹
		 */
		protected abstract void bundleMapping(YWriteBundle bundle);
	}

}
