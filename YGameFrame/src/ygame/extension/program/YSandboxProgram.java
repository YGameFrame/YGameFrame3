package ygame.extension.program;

import ygame.framework.R;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YReadBundle;
import ygame.utils.YTextFileUtils;
import android.content.res.Resources;
import android.opengl.GLES20;

/**
 * <b>WebGL着色程序</b>
 * 
 * <p>
 * <b>概述</b>：您可以将<a
 * href="http://glsl.heroku.com">http
 * ://glsl.heroku.com</a>上的着色脚本加载进该程序。
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
public class YSandboxProgram extends YABlankProgram
{
	private float time;
	private float fVelocity = 1;
	private int uTime = -1;
	private int uResolution = -1;
	private float iFlag = 1;

	/**
	 * @param resources
	 *                资源
	 * @param strFragmentShaderSrc
	 *                网站<a
	 *                href="http://glsl.heroku.com"
	 *                >http
	 *                ://glsl.heroku.com</a>
	 *                上的片元着色器代码
	 * @param fVelocity
	 *                图像表面变化速度
	 */
	public YSandboxProgram(Resources resources,
			String strFragmentShaderSrc, float fVelocity)
	{
		super(resources, GLES20.GL_TRIANGLES);
		fillCodeAndParam(YTextFileUtils.getStringFromResRaw(
				R.raw.sandbox_vsh, resources),
				strFragmentShaderSrc, YSandboxAdapter.class);
		this.fVelocity = fVelocity;
	}

	@Override
	protected void onInitialize(int iProgramHandle)
	{
		super.onInitialize(iProgramHandle);
		uTime = getUniform("time");
		uResolution = getUniform("resolution");
	}

	@Override
	protected void applyParams(int iProgramHandle, YReadBundle bundle,
			YSystem system)
	{
		if (-1 != uTime)
			bindUniformf(uTime, (time = time + iFlag) * fVelocity);
		if (-1 != uResolution)
			bindUniformf(uResolution, system.YVIEW.getWidth(),
					system.YVIEW.getHeight());
		super.applyParams(iProgramHandle, bundle, system);
	}

	/**
	 * <b>WebGL参数适配器</b>
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
	public static final class YSandboxAdapter extends
			YABlankProgram.YABlankAdapter<YSandboxAdapter>
	{

	}
}
