package ygame.domain;

import ygame.framework.core.YCamera;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YADomainLogic;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;

/**
 * <b>实体逻辑</b>
 * 
 * <p>
 * <b>概述</b>： 在每个周期，您应该通过调用
 * {@link YDomain#getParametersAdapter()}得到
 * <b>参数适配器</b>，向其填充参数，从而操作每一帧画面的渲染。参见
 * {@link #onCycle(double, YDomain, YWriteBundle, YSystem, YScene, YCamera, YMatrix, YMatrix, YMatrix)}
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
public abstract class YDomainLogic extends YADomainLogic
{
	// protected final YTransformer transformer =
	// new YTransformer();

	@Override
	protected final void onClockCycle(double dbElapseTime_s,
			YBaseDomain domainContext, YWriteBundle bundle,
			YSystem system, YScene sceneCurrent, YCamera camera,
			YMatrix matrix4vp, YMatrix matrix4Projection,
			YMatrix matrix4View)
	{
		YDomain domain = (YDomain) domainContext;
		onCycle(dbElapseTime_s, domain, bundle, system, sceneCurrent,
				camera, matrix4vp, matrix4Projection,
				matrix4View);
		domain.getParametersAdapter().bundleMapping(bundle);
		// mVMatrix = camera.getViewMatrix();
		// mPMatrix =
		// camera.getProjectionMatrix();
		// // Pre-multiply View and
		// Projection
		// // matricies once for speed
		// mVPMatrix =
		// mPMatrix.clone().multiply(mVMatrix);
		// mInvVPMatrix.setAll(mVPMatrix).inverse();
		// camera.updateFrustum(mInvVPMatrix);
		// // update

		// transformer.calculate(mVPMatrix,
		// mPMatrix, mVMatrix, null,
		// bundle);
	}

	/**
	 * <b>系统</b>{@link YSystem}
	 * 启动后，定时计算每一帧的画面渲染参数。 您应该通过复写该方法，计算出这些参数，并调用
	 * {@link YDomain#getParametersAdapter()}
	 * 获得<b>参数适配器</b>， 将计算的参数依次填入，从而完成画面渲染。
	 * 
	 * @param dbElapseTime_s
	 * @param domainContext
	 * @param bundle
	 * @param system
	 * @param sceneCurrent
	 * @param camera
	 * @param matrix4vp
	 * @param matrix4Projection
	 * @param matrix4View
	 */
	protected abstract void onCycle(double dbElapseTime_s,
			YDomain domainContext, YWriteBundle bundle,
			YSystem system, YScene sceneCurrent, YCamera camera,
			YMatrix matrix4vp, YMatrix matrix4Projection,
			YMatrix matrix4View);

}
