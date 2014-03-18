package ygame.framework.domain;

import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.request.YRequest;
import ygame.math.YMatrix;

/**
 * <b>抽象基础实体逻辑</b>
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
public abstract class YABaseDomainLogic
{
	final YWriteBundle bundle = new YWriteBundle();

	void onClockCycle(double dbElapseTime_s, YBaseDomain domainContext,
			YSystem system, YScene sceneCurrent, YMatrix matrix4PV,
			YMatrix matrix4Projection, YMatrix matrix4View)
	{
		onClockCycle(dbElapseTime_s, domainContext, bundle, system,
				sceneCurrent, matrix4PV, matrix4Projection,
				matrix4View);
	}

	/**
	 * 时钟周期到来时该函数将会被调用，可将逻辑计算的绘图结果写入<b>可写包裹</b> {@link YWriteBundle}中
	 * 
	 * @param dbElapseTime_s
	 *                两周期之间的流逝的时间，秒为单位
	 * @param domainContext
	 *                所隶属的实体，实体上下文，可类型转换为所属实体类型
	 * @param bundle
	 *                可写包裹，包裹中的内容将会被对应的<b>实体视图</b >{@link YABaseDomainView}
	 *                收到
	 * @param system
	 *                系统
	 * @param sceneCurrent
	 *                当前场景
	 * @param matrix4PV
	 *                “视图-投影矩阵”
	 * @param matrix4Projection
	 *                “投影矩阵”
	 * @param matrix4View
	 *                “视图矩阵”
	 */
	abstract protected void onClockCycle(double dbElapseTime_s,
			YBaseDomain domainContext, YWriteBundle bundle,
			YSystem system, YScene sceneCurrent, YMatrix matrix4PV,
			YMatrix matrix4Projection, YMatrix matrix4View);

	/**
	 * 在此处处理对象收到的<b>请求</b>{@link YRequest} ，应将<b>请求</b>转化为对应的子类型
	 * 
	 * @param request
	 *                请求
	 * @param system
	 *                系统
	 * @param sceneCurrent
	 *                当前所处场景
	 */
	abstract protected void onDealRequest(YRequest request, YSystem system,
			YScene sceneCurrent);

	/**
	 * 参见{@link YBaseDomain#onPreframe()}
	 * 
	 * @param domainContext
	 *                实体上下文
	 */
	protected void onPreframe(YBaseDomain domainContext)
	{
	}

	/**
	 * 当实体被添加进场景时该函数被回调
	 * 
	 * @param system
	 *                系统
	 * @param domainContext
	 *                实体上下文
	 */
	protected void onAttach(YSystem system, YBaseDomain domainContext)
	{
	}

}
