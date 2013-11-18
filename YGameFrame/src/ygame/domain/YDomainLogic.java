package ygame.domain;

import ygame.framework.core.YCamera;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YADomainLogic;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YWriteBundle;
import ygame.math.Matrix4;

/**
 * <b>实体逻辑</b>
 * 
 * <p>
 * <b>概述</b>： 该对象中有一个<b>变换子</b>{@link YTransformer}——
 * {@link #transformer}，
 * 您可以使用它方便地对物体实施几何变换，如位移、旋转、缩放等，从而做出不错的 动画效果。
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
	private Matrix4 mVMatrix;
	private Matrix4 mPMatrix;
	private Matrix4 mVPMatrix;
	private Matrix4 mInvVPMatrix = new Matrix4();

	protected final YTransformer transformer = new YTransformer();

	@Override
	protected final void onClockCycle(double dbElapseTime_s,
			YBaseDomain domainContext, YWriteBundle bundle,
			YSystem system, YScene sceneCurrent, YCamera camera,
			Matrix4 matrix4vp, Matrix4 matrix4Projection,
			Matrix4 matrix4View)
	{
		onCycle(dbElapseTime_s, domainContext, bundle, system,
				sceneCurrent, camera, matrix4vp,
				matrix4Projection, matrix4View);
		mVMatrix = camera.getViewMatrix();
		mPMatrix = camera.getProjectionMatrix();
		// Pre-multiply View and Projection
		// matricies once for speed
		mVPMatrix = mPMatrix.clone().multiply(mVMatrix);
		mInvVPMatrix.setAll(mVPMatrix).inverse();
		camera.updateFrustum(mInvVPMatrix); // update

		transformer.calculate(mVPMatrix, mPMatrix, mVMatrix, null,
				bundle);
	}

	protected abstract void onCycle(double dbElapseTime_s,
			YBaseDomain domainContext, YWriteBundle bundle,
			YSystem system, YScene sceneCurrent, YCamera camera,
			Matrix4 matrix4vp, Matrix4 matrix4Projection,
			Matrix4 matrix4View);

}
