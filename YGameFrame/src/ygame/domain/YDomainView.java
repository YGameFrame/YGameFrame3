package ygame.domain;

import android.opengl.GLES20;
import ygame.framework.core.YGL_Configuration;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YABaseDomainView;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YReadBundle;

/**
 * <b>实体视图</b>
 * 
 * <p>
 * <b>概述</b>： 该视图使用您中传入的<b>着色程序</b>
 * {@link YABaseShaderProgram}进行渲染
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
public class YDomainView extends YABaseDomainView
{
	YABaseShaderProgram program;
	
	public int iDrawMode = GLES20.GL_TRIANGLES;

	/**
	 * @param program
	 *                渲染视图所使用的<b>渲染程序</b>
	 */
	public YDomainView(YABaseShaderProgram program)
	{
		this.program = program;
	}

	/**
	 * 指定渲染该视图的<b>渲染程序</b>{@link YABaseShaderProgram}
	 * 
	 * @param program
	 *                渲染程序
	 */
	public void useProgram(YABaseShaderProgram program)
	{
		this.program = program;
	}

	@Override
	protected void onDraw(YReadBundle bundle, YBaseDomain domainContext , YSystem system)
	{
		program.startRendering();
		program.applyParams(program.iProgramHandle, bundle , system , this);
		program.endRendering();
	}

	@Override
	protected void onGL_Initialize(YSystem system,
			YGL_Configuration configurationGL, int iWidth,
			int iHeight)
	{
		program.initialize();
	}

}
