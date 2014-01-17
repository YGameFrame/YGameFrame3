package ygame.framework.domain;

import ygame.framework.YAStateMachineContext;
import ygame.framework.core.YCamera;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.request.YRequest;
import ygame.math.YMatrix;

/**
 * <b>实体逻辑</b>
 * 
 * <p>
 * <b>概述</b>： 负责实体逻辑方面的计算
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
public abstract class YADomainLogic extends YAStateMachineContext
{
	final YWriteBundle bundle = new YWriteBundle();

	void onClockCycle(double dbElapseTime_s, YBaseDomain domainContext,
			YSystem system, YScene sceneCurrent, YCamera camera,
			YMatrix matrix4vp, YMatrix matrix4Projection,
			YMatrix matrix4View)
	{
		onClockCycle(dbElapseTime_s, domainContext, bundle, system,
				sceneCurrent, camera, matrix4vp,
				matrix4Projection, matrix4View);
	}

	@Override
	final protected void inputRequest(YRequest request)
	{
		onDealRequest(request);
	}

	/**
	 * 时钟周期到来时该函数将会被调用，可将逻辑计算的绘图结果写入<b>可写包裹</b>
	 * {@link YWriteBundle}中
	 * 
	 * @param dbElapseTime_s
	 *                两周期之间的流逝的时间，秒为单位
	 * @param domainContext
	 *                所隶属的实体，实体上下文，可类型转换为所属实体类型
	 * @param bundle
	 *                可写包裹，包裹中的内容将会被对应的<b>实体视图</b
	 *                >{@link YADomainView}收到
	 * @param system
	 *                系统
	 * @param sceneCurrent
	 *                当前场景
	 * @param camera
	 *                摄像机
	 * @param matrix4VP
	 *                “视图-投影矩阵”
	 * @param matrix4Projection
	 *                “投影矩阵”
	 * @param matrix4View
	 *                “视图矩阵”
	 */
	abstract protected void onClockCycle(double dbElapseTime_s,
			YBaseDomain domainContext, YWriteBundle bundle,
			YSystem system, YScene sceneCurrent, YCamera camera,
			YMatrix matrix4VP, YMatrix matrix4Projection,
			YMatrix matrix4View);

	/**
	 * 在此处处理对象收到的<b>请求</b>{@link YRequest}
	 * ，应将<b>请求</b>转化为对应的子类型
	 * 
	 * @param request
	 *                请求
	 */
	abstract protected void onDealRequest(YRequest request);

	/**
	 * 参见{@link YBaseDomain#onPreframe()}
	 * 
	 * @param domainContext
	 *                实体上下文
	 */
	protected void onPreframe(YBaseDomain domainContext)
	{
	}

}
