package ygame.framework.domain;

import ygame.framework.core.YABaseDomain;
import ygame.framework.core.YGL_Configuration;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.request.YRequest;
import ygame.math.YMatrix;
import android.annotation.SuppressLint;

/**
 * <b>实体</b>
 * 
 * <p>
 * <b>概述</b>： <b>基础实体</b>{@link YABaseDomain} 的一个实现：将计算与渲染分别交给 <b>实体逻辑</b>
 * {@link YABaseDomainLogic} 与<b>实体视图</b> {@link YABaseDomainView}
 * 进行。即该对象实现了一种分工模型。
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
public class YBaseDomain extends YABaseDomain
{
	// XXX 内存块容量尚未完成可调设置
	private Object[] map1 = new Object[20];
	private Object[] map2 = new Object[20];

	protected final YABaseDomainLogic logic;
	protected final YABaseDomainView view;

	private boolean bSide;

	/**
	 * @param logic
	 *                实体逻辑，不可为空
	 * @param view
	 *                实体视图，不可为空
	 */
	// TODO 非空检查为完成
	public YBaseDomain(String KEY, YABaseDomainLogic logic,
			YABaseDomainView view)
	{
		super(KEY);
		this.logic = logic;
		this.view = view;

		logic.bundle.map = map1;
		view.bundle.map = map2;
	}

	@Override
	protected void onPreframe()
	{
		// 交换内存块
		swapMem();

		logic.onPreframe(this);
		view.onPreframe(this);
	}

	private void swapMem()
	{
		bSide = !bSide;
		if (bSide)
		{
			logic.bundle.map = map1;
			view.bundle.map = map2;
		} else
		{
			logic.bundle.map = map2;
			view.bundle.map = map1;
		}
	}

	@SuppressLint("WrongCall")
	@Override
	protected void onDraw(YSystem system)
	{
		view.onDraw(this, system);
	}

	@Override
	protected void onClockCycle(double dbElapseTime_s, YSystem system,
			YScene sceneCurrent, YMatrix matrix4PV,
			YMatrix matrix4Projection, YMatrix matrix4View)
	{
		logic.onClockCycle(dbElapseTime_s, this, system, sceneCurrent,
				matrix4PV, matrix4Projection, matrix4View);
	}

	@Override
	protected void onReceiveRequest(YRequest request, YSystem system,
			YScene sceneCurrent)
	{
		logic.onDealRequest(request, system, sceneCurrent);
	}

	@Override
	protected void onGL_Initialize(YSystem system,
			YGL_Configuration configurationGL, int iWidth,
			int iHeight)
	{
		view.onGL_Initialize(system, configurationGL, iWidth, iHeight);
	}

	@Override
	protected void onAttach(YSystem system)
	{
		logic.onAttach(system, this);
	}

}
