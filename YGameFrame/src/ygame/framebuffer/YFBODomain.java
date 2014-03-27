package ygame.framebuffer;

import ygame.domain.YADomainLogic;
import ygame.domain.YDomain;
import ygame.domain.YDomainView;
import ygame.exception.YException;
import ygame.framework.core.YCamera;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.math.YMatrix;
import ygame.texture.YTexture;

/**
 * <b>帧缓冲处理实体</b>
 * 
 * <p>
 * <b>概述</b>：该对象应该被添加到<b>备有帧缓冲的场景</b>{@link YFBOScene}
 * （或其子类）中，该对象将整个场景视为一幅<b>纹理</b>{@link YTexture}，并对其处理，然后渲染到屏幕上以完成场景级特效。
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>：
 * <li>只有当该实体所处的场景允许将场景视为纹理时（参见 {@link YFBOScene#enableSceneAsTexture(boolean)}
 * ），该对象才会被激活，否则该对象休眠。
 * <li>该实体采用自带<b>摄像机</b>{@link YCamera}，并非场景的摄像机！
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public class YFBODomain extends YDomain
{

	public YFBODomain(String KEY, YADomainLogic logic, YDomainView view)
	{
		super(KEY, logic, view);
	}

	@Override
	protected void onEnterScene(YScene scene)
	{
		if (!(scene instanceof YFBOScene))
			throw new YException("当前该实体被添加到："
					+ scene.getClass().getName() + "，不是"
					+ YFBOScene.class.getName() + "或其子类！",
					getClass().getName(),
					"该实体应该被添加到" + YFBOScene.class.getName()
							+ "中（或其子类），用于整个场景的处理。");
		super.onEnterScene(scene);
	}

	@Override
	protected void onAttach(YSystem system)
	{
		super.onAttach(system);
		camera.setProjectionMatrix(system.YVIEW.getWidth(),
				system.YVIEW.getHeight());
	}

	private YMatrix matrixPV = new YMatrix();
	private YCamera camera = new YCamera();

	@Override
	protected void onClockCycle(double dbElapseTime_s, YSystem system,
			YScene sceneCurrent, YMatrix matrix4pv,
			YMatrix matrix4Projection, YMatrix matrix4View)
	{
		YFBOScene scene = (YFBOScene) system.getCurrentScene();
		if (scene.bEnableFBO)
		{
			YMatrix.multiplyMM(matrixPV, camera.getProjectMatrix(),
					camera.getViewMatrix());
			super.onClockCycle(dbElapseTime_s, system,
					sceneCurrent, matrixPV,
					camera.getProjectMatrix(),
					camera.getViewMatrix());
		}
	}

	@Override
	protected void onDraw(YSystem system)
	{
		YFBOScene scene = (YFBOScene) system.getCurrentScene();
		if (scene.bEnableFBO)
		{
			YFrameBuffer frameBuffer = scene.getFrameBuffer();
			frameBuffer.end(system);
			super.onDraw(system);
			frameBuffer.clear(system);
			frameBuffer.begin();
		}
	}

}
