package ygame.framework.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ygame.framework.YAStateMachineContext;
import ygame.framework.request.YRequest;
import ygame.framework.request.YRequest.YWhen;
import ygame.math.YMatrix;
import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.util.Log;

/**
 * <b>场景</b>
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
public class YScene extends YAStateMachineContext
{
	private static final String strTAG = "YScene";

	@SuppressLint("UseSparseArrays")
	private final Map<String, YABaseDomain> mapDomains = new HashMap<String, YABaseDomain>();

	private YCamera camera = new YCamera();
	private final YMatrix matrixPV = new YMatrix();

	private final YSystem SYSTEM;

	public YScene(YSystem system)
	{
		this.SYSTEM = system;
	}

	@Override
	protected void inputRequest(YRequest request)
	{
		if (request instanceof YSceneRequest)
		{
			dealRequest((YSceneRequest) request);
			return;
		}
		if (request instanceof YDomainRequest)
		{
			YDomainRequest domainRequest = (YDomainRequest) request;
			mapDomains.get(domainRequest.WHO).inputRequest(
					domainRequest);
			return;
		}
		Log.e(strTAG, "系统收到了不能解析的请求" + request.getClass().getName());
		throw new RuntimeException("系统收到不能解析的请求");
	}

	private void dealRequest(YSceneRequest request)
	{
		switch (request.iKEY)
		{
		case YSceneRequest.KEY.iADD_DOMAINS:
			internalAddDomains(request.domains);
			break;

		case YSceneRequest.KEY.iREMOVE_DOMAINS:
			internalRemoveDomains(request.domains);
			break;

		default:
			break;
		}
	}

	/**
	 * 向场景添加实体
	 * 
	 * @param domains
	 *                实体（可多个）
	 */
	public void addDomains(YABaseDomain... domains)
	{
		YSceneRequest request = new YSceneRequest(
				YSceneRequest.KEY.iADD_DOMAINS,
				YWhen.BEFORE_RENDER);
		request.domains = domains;
		SYSTEM.inputRequest(request);
		// internalAddDomains(domains);
	}

	private void internalAddDomains(YABaseDomain[] domains)
	{
		for (YABaseDomain domain : domains)
			mapDomains.put(domain.KEY, domain);
	}

	/**
	 * 从场景中移除实体
	 * 
	 * @param domains
	 *                需要移除的实体（可多个）
	 */
	public void removeDomains(YABaseDomain... domains)
	{
		YSceneRequest request = new YSceneRequest(
				YSceneRequest.KEY.iREMOVE_DOMAINS,
				YWhen.BEFORE_RENDER);
		request.domains = domains;
		SYSTEM.inputRequest(request);
	}

	/**
	 * 从场景中移除实体
	 * 
	 * @param domainKEYs
	 *                需要移除的实体的键值（可多个）
	 */
	public void removeDomains(String... domainKEYs)
	{
		YABaseDomain[] domains = new YABaseDomain[domainKEYs.length];
		for (int i = 0; i < domainKEYs.length; i++)
			domains[i] = mapDomains.get(domainKEYs[i]);

		removeDomains(domains);
	}

	private void internalRemoveDomains(YABaseDomain[] domains)
	{
		for (YABaseDomain domain : domains)
			mapDomains.remove(domain.KEY);
	}

	/**
	 * 获取当前摄像机
	 * 
	 * @return 场景当前使用的摄像机
	 */
	public YCamera getCurrentCamera()
	{
		return camera;
	}

	// private int iFlagTest = 1;

	protected void onClockCycle(double dbDeltaTime_s)
	{
		// float z = camera.getZ();
		// if (z > 60)
		// iFlagTest = -1;
		// else if (z < 1)
		// iFlagTest = 1;
		// camera.setZ((float) (z + iFlagTest
		// * dbDeltaTime_s * 5));
		//
		// camera.setAngle((float)
		// (camera.getAngle() + dbDeltaTime_s
		// * 20));

		YMatrix.multiplyMM(matrixPV, camera.getProjectMatrix(),
				camera.getViewMatrix());
		Collection<YABaseDomain> domains = mapDomains.values();
		for (YABaseDomain domain : domains)
			domain.onClockCycle(dbDeltaTime_s, SYSTEM, this,
					matrixPV, camera.getProjectMatrix(),
					camera.getViewMatrix());
	}

	protected void onPreframe()
	{
		Collection<YABaseDomain> domains = mapDomains.values();
		for (YABaseDomain domain : domains)
			domain.onPreframe();
	}

	protected void onGL_Initialize(YGL_Configuration configurationGL,
			int iWidth, int iHeight)
	{
		camera.setProjectionMatrix(iWidth, iHeight);
		Collection<YABaseDomain> domains = mapDomains.values();
		for (YABaseDomain domain : domains)
			domain.onGL_Initialize(SYSTEM, configurationGL, iWidth,
					iHeight);
	}

	@SuppressLint("WrongCall")
	protected void onDraw()
	{
		int clearMask = GLES20.GL_COLOR_BUFFER_BIT;
		clearMask |= GLES20.GL_DEPTH_BUFFER_BIT;
		GLES20.glClear(clearMask);

		Collection<YABaseDomain> domains = mapDomains.values();
		for (YABaseDomain domain : domains)
			domain.onDraw();
	}

	private static class YSceneRequest extends YRequest
	{

		private static final class KEY
		{
			private static final int iADD_DOMAINS = 1;
			private static final int iREMOVE_DOMAINS = 2;
		}

		private YABaseDomain[] domains;

		private YSceneRequest(int iKEY, YWhen when)
		{
			super(iKEY, when);
		}
	}

}
