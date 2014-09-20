package ygame.domain;

import ygame.framework.core.YSystem;
import ygame.framework.domain.YReadBundle;
import ygame.framework.domain.YWriteBundle;
import ygame.skeleton.YSkeleton;
import ygame.utils.YShaderUtils;
import android.opengl.GLES20;

/**
 * <b>基础抽象着色（渲染）程序</b>
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
 */
public abstract class YABaseShaderProgram
{
	int iProgramHandle = -1;

	private String strFragmentShaderCode;

	private String strVertexShaderCode;

	YABaseParametersAdapter daParamsAdapter;

	protected YABaseShaderProgram()
	{
	}

	/**
	 * @param strVertexShaderSrc
	 *                顶点着色器脚本代码
	 * @param strFragmentShaderSrc
	 *                片元着色器脚本代码
	 * @param clazzDataAdapter
	 *                该渲染程序的参数适配器类
	 */
	final protected void fillCodeAndParam(String strVertexShaderSrc,
			String strFragmentShaderSrc, Class<?> clazzDataAdapter)
	{
		this.strFragmentShaderCode = strFragmentShaderSrc;
		this.strVertexShaderCode = strVertexShaderSrc;
		this.daParamsAdapter = createDataAdapter(clazzDataAdapter);
	}

	void startRendering()
	{
		if (!GLES20.glIsProgram(iProgramHandle))
			initialize();
		GLES20.glUseProgram(iProgramHandle);
	}

	void initialize()
	{
		System.out.println("初始化，当前句柄： " + iProgramHandle);
		// if (-1 == iProgramHandle||
		// !GLES20.glIsProgram(iProgramHandle))
		// return;
		// 创建程序，更新程序句柄
		iProgramHandle = YShaderUtils.createProgram(
				strVertexShaderCode, strFragmentShaderCode);
		onInitialize(iProgramHandle);
	}

	void endRendering()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private YABaseParametersAdapter createDataAdapter(
			Class<?> clazzDataAdapter)
	{
		if (null == clazzDataAdapter)
			return null;
		try
		{
			return (YABaseParametersAdapter) clazzDataAdapter
					.newInstance();
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
	 * 您可以通过复写该方法利用给定参数“程序句柄”—— iProgramHandle来获取相应的 着色器变量句柄。
	 * 
	 * @param iProgramHandle
	 *                着色程序句柄
	 */
	abstract protected void onInitialize(int iProgramHandle);

	/**
	 * 您可以通过复写该方法向着色脚本的变量传递值</br>
	 * 
	 * @param iProgramHandle
	 *                着色程序句柄
	 * @param bundle
	 *                可读包裹
	 * @param system
	 *                系统
	 * @param domainView
	 *                当前渲染的实体视图
	 */
	abstract protected void applyParams(int iProgramHandle,
			YReadBundle bundle, YSystem system,
			YDomainView domainView);

	/**
	 * 将顶点缓冲对象索引（即数据源）与渲染程序中的变量绑定
	 * 
	 * @param iDataSrcHandle
	 *                顶点缓冲（即数据源）在开放图形库（OpenGL ES 2.0）中的句柄
	 * @param iValueHandle
	 *                渲染程序中变量的句柄
	 * @param iFloatsPerValue
	 *                该渲染程序变量由几个浮点数（float）组成
	 */
	public static void bindVBODataSourcToValue(int iDataSrcHandle,
			int iValueHandle, int iFloatsPerValue)
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, iDataSrcHandle);

		GLES20.glEnableVertexAttribArray(iValueHandle);
		GLES20.glVertexAttribPointer(iValueHandle, iFloatsPerValue,
				GLES20.GL_FLOAT, false, 0, 0);
	}

	/**
	 * 使用索引缓冲对象绘制画面
	 * 
	 * @param iIBODataSrcHandle
	 *                索引缓冲（即数据源）在开放图形库（OpenGL ES 2.0）中的句柄
	 * @param iVertexNum
	 *                要绘制的顶点的个数
	 * @param iDrawMode
	 *                绘制的图元装配模式
	 */
	public static void drawWithIBO(int iIBODataSrcHandle, int iVertexNum,
			int iDrawMode)
	{
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,
				iIBODataSrcHandle);
		GLES20.glDrawElements(iDrawMode, iVertexNum,
				GLES20.GL_UNSIGNED_SHORT, 0);
	}

	public static void drawWithVBO(int iVertexNum, int iDrawMode)
	{
		// Clear the currently bound buffer (so future OpenGL calls do
		// not use this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glDrawArrays(iDrawMode, 0, iVertexNum);
	}

	/**
	 * <b>参数适配器</b>
	 * 
	 * <p>
	 * <b>概述</b>：这是<b>渲染程序</b>的参数适配器。</br>
	 * <li>作为使用者，您应该依次填写适配器中所声明的参数；
	 * <li>如果您开发了一个<b> 渲染程序</b> ，您应该再开发本类 {@link YABaseParametersAdapter}
	 * 的一个子类， 用来向外界声明使用此<b>渲染程序</b>时应该向其传递怎样的参数。同时注意该类只应有无参构造函数。
	 * 
	 * <p>
	 * <b>建议</b>： TODO
	 * 
	 * <p>
	 * <b>详细</b>： TODO
	 * 
	 * <p>
	 * <b>注</b>：
	 * <li>作为使用者，为了确保数据的正确，您每次都应该对<b>适配器</b >中声明的参数<b>全部重新</b>填写一遍。
	 * <li>该类的子类只能有<b>无参构造函数</b>
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
	public static abstract class YABaseParametersAdapter
	{
		protected YABaseParametersAdapter()
		{
		}

		/**
		 * 将<b>适配器</b>中的属性依次映射进<b>可写包裹</b> {@link YWriteBundle}
		 * 
		 * @param bundle
		 *                可写包裹
		 */
		protected abstract void bundleMapping(YWriteBundle bundle);
	}

}
