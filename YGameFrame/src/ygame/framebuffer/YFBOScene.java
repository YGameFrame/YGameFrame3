package ygame.framebuffer;

import ygame.exception.YException;
import ygame.framework.core.YGL_Configuration;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.texture.YTexture;

/**
 * <b>备有帧缓冲的场景</b>
 * 
 * <p>
 * <b>概述</b>： 该场景备有可供离屏渲染的帧缓冲对象，您可以将此场景视作<b>纹理</b>{@link YTexture}
 * 进而对其重新处理，以做出场景级别的特效（例如场景切换）。您应该通过复写<b>帧缓冲处理实体</b>{@link YFBODomain}
 * 的子类并将其添入该场景来控制其特效逻辑。
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
public class YFBOScene extends YScene
{
	private static YFrameBuffer frameBuffer;

	private static float fRadio = 0.7f;
	private volatile static float fNewRadio = fRadio;
	volatile boolean bEnableFBO;

	public YFBOScene(YSystem system, String strName)
	{
		super(system, strName);
	}

	@Override
	protected void onGL_Initialize(YGL_Configuration configurationGL,
			int iWidth, int iHeight)
	{
		super.onGL_Initialize(configurationGL, iWidth, iHeight);
		getFrameBuffer();
	}

	@Override
	protected void onDraw()
	{
		if (bEnableFBO)
		{
			getFrameBuffer().begin();
			super.onDraw();
			getFrameBuffer().end(SYSTEM);
		} else
			super.onDraw();
	}

	/**
	 * 允许或禁止场景作为纹理
	 * 
	 * @param bEnable
	 *                真为允许、反之禁止
	 */
	public void enableSceneAsTexture(boolean bEnable)
	{
		this.bEnableFBO = bEnable;
	}

	/**
	 * 将场景视为一幅<b>纹理</b>{@link YTexture}，从而您可以对整个场景做其他处理<br>
	 * <b>注：</b>您应该首先调用{@link #enableSceneAsTexture(boolean)}
	 * 来开启该模式，否则将抛出异常。值得注意的是，当场景处于 “正在进入”、“正在退出”状态下，该模式是默认开启，“运行状态”下则是默认关闭的。
	 * 
	 * @return 以场景为内容的纹理
	 */
	public YTexture asTexture()
	{
		if (!bEnableFBO)
			throw new YException("目前尚未允许将场景转为纹理", getClass()
					.getName(),
					"请调用enableSceneAsTexture(true)以示开启！");
		return getFrameBuffer().getTexture();
	}

	@Override
	protected void willRun()
	{
		enableSceneAsTexture(false);
		super.willRun();
	}

	@Override
	protected void willUnmount()
	{
		super.willUnmount();
		// enableSceneAsTexture(false);
	}

	@Override
	protected void willQuit()
	{
		enableSceneAsTexture(true);
		super.willQuit();
	}

	@Override
	protected void willEnter()
	{
		enableSceneAsTexture(true);
		super.willEnter();
	}

	YFrameBuffer getFrameBuffer()
	{
		if (null == frameBuffer)
		{
			fRadio = fNewRadio;
			frameBuffer = new YFrameBuffer(
					(int) (SYSTEM.YVIEW.getWidth() * fRadio),
					(int) (SYSTEM.YVIEW.getHeight() * fRadio));
		} else
		{
			if (fRadio != fNewRadio)
			{
				fRadio = fNewRadio;
				frameBuffer.destroy();
				frameBuffer = new YFrameBuffer(
						(int) (SYSTEM.YVIEW.getWidth() * fRadio),
						(int) (SYSTEM.YVIEW.getHeight() * fRadio));
			}
		}
		return frameBuffer;
	}

	/**
	 * 设置场景离屏渲染缓冲的质量，数值越高品质越高、但处理速度变慢，默认为0.7
	 * 
	 * @param fQuality
	 *                品质参数
	 */
	public static void setFrameBufferQuality(float fQuality)
	{
		if (fQuality <= 0)
			throw new YException("品质参数<=0！",
					YFBOScene.class.getName(),
					"品质参数至少应该大于零！");
		fNewRadio = fQuality;
	}
}
