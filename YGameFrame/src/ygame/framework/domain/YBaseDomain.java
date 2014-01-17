package ygame.framework.domain;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import ygame.framework.core.YABaseDomain;
import ygame.framework.core.YCamera;
import ygame.framework.core.YGL_Configuration;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.request.YRequest;
import ygame.math.YMatrix;

/**
 * <b>实体</b>
 * 
 * <p>
 * <b>概述</b>： <b>基础实体</b>{@link YABaseDomain}
 * 的一个实现：将计算与渲染分别交给 <b>实体逻辑</b>{@link YADomainLogic}
 * 与<b>实体视图</b>{@link YADomainView}进行。即该对象实现了一种分工模型。
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
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Object> map1 = new HashMap<Integer, Object>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Object> map2 = new HashMap<Integer, Object>();

	private YADomainLogic logic;
	private YADomainView view;

	private boolean bSide;

	/**
	 * @param logic
	 *                实体逻辑，不可为空
	 * @param view
	 *                实体视图，不可为空
	 */
	// TODO 非空检查为完成
	public YBaseDomain(int iKEY, YADomainLogic logic, YADomainView view)
	{
		super(iKEY);
		this.logic = logic;
		this.view = view;
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
	protected void onDraw()
	{
		view.onDraw(this);
	}

	@Override
	protected void onClockCycle(double dbElapseTime_s, YSystem system,
			YScene sceneCurrent, YCamera camera, YMatrix matrix4VP,
			YMatrix matrix4Projection, YMatrix matrix4View)
	{
		logic.onClockCycle(dbElapseTime_s, this, system, sceneCurrent,
				camera, matrix4VP, matrix4Projection,
				matrix4View);
	}

	@Override
	protected void inputRequest(YRequest request)
	{
		logic.inputRequest(request);
	}

	@Override
	protected void onGL_Initialize(YSystem system,
			YGL_Configuration configurationGL, int iWidth,
			int iHeight)
	{
		view.onGL_Initialize(system, configurationGL, iWidth, iHeight);
	}

}
