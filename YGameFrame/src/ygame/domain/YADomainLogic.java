package ygame.domain;

import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YABaseDomainLogic;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;

/**
 * <b>实体逻辑</b>
 * 
 * <p>
 * <b>概述</b>： 在每个周期，您应该通过调用 {@link YDomain#getParametersAdapter()}得到
 * <b>参数适配器</b>，向其填充参数，从而操作每一帧画面的渲染。参见
 * {@link #onCycle(double, YDomain, YWriteBundle, YSystem, YScene, YMatrix, YMatrix, YMatrix)}
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
 * <b>例</b>：如下复写方法，计算逻辑值：</br> <code>
 protected void onCycle(double
 *                dbElapseTime_s, ...)</br> {
 *               <dd>//计算逻辑值</br>
 *                <dd>...</br>
 *                <dd>mover.setZ(mover.getZ() *
 *                iFlag * fDel1);</tr></br>
 *                <dd>mover.setY(mover.getY() * iFlag *
 *                fDel1);</br>
 * <dd>...</br>
 * 		<dd>//计算完毕后，填写渲染程序的参数，注意要全部重新填写一遍</br>
 *                <dd>YSimpleParamAdapter dataAdapter =
 *                (YSimpleParamAdapter) domainContext
 *                .getParametersAdapter();</br>
 *                <dd>dataAdapter.paramMatrixPV(matrix4PV
 *                ).paramMover(mover)
 *                .paramSkeleton(skeleton);</br>}</code>
 * 
 * @author yunzhong
 * 
 */
public abstract class YADomainLogic extends YABaseDomainLogic
{
	@Override
	protected final void onClockCycle(double dbElapseTime_s,
			YBaseDomain domainContext, YWriteBundle bundle,
			YSystem system, YScene sceneCurrent, YMatrix matrix4PV,
			YMatrix matrix4Projection, YMatrix matrix4View)
	{
		YDomain domain = (YDomain) domainContext;
		onCycle(dbElapseTime_s, domain, bundle, system, sceneCurrent,
				matrix4PV, matrix4Projection, matrix4View);
		domain.getParametersAdapter().bundleMapping(bundle);
	}

	/**
	 * * <b>系统</b>{@link YSystem} 启动后，定时计算每一帧的画面渲染参数。 您应该通过复写该方法，计算出这些参数，并调用
	 * {@link YDomain#getParametersAdapter()} 获得<b>参数适配器</b>，
	 * 将计算的参数依次填入，从而完成画面渲染。
	 * 
	 * @param dbElapseTime_s
	 *                两周期之间的流逝的时间，秒为单位
	 * @param domainContext
	 *                实体上下文
	 * @param bundle
	 *                可写包裹
	 * @param system
	 *                引擎系统
	 * @param sceneCurrent
	 *                当前场景
	 * @param matrix4PV
	 *                投影-视图联合矩阵
	 * @param matrix4Projection
	 *                投影矩阵
	 * @param matrix4View
	 *                视图矩阵
	 */
	protected abstract void onCycle(double dbElapseTime_s,
			YDomain domainContext, YWriteBundle bundle,
			YSystem system, YScene sceneCurrent, YMatrix matrix4PV,
			YMatrix matrix4Projection, YMatrix matrix4View);

}
