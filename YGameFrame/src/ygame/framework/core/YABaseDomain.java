package ygame.framework.core;

import ygame.framework.YAStateMachineContext;
import ygame.math.Matrix4;

/**
 * <b>基本实体</b>
 * 
 * <p>
 * <b>概述</b>： 提供给系统的调用界面
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
public abstract class YABaseDomain extends YAStateMachineContext
{
	/** <b>实体标识</b> */
	public final int iKEY;

	/**
	 * @param iKEY
	 *                参阅{@link #iKEY}
	 */
	public YABaseDomain(int iKEY)
	{
		this.iKEY = iKEY;
	}

	/**
	 * 交换内存块时回调此函数
	 * 
	 * @param bSide
	 *                本次交换内存所处的“标识面”
	 */
	protected abstract void onSwap(boolean bSide);

	/**
	 * 绘图时回调此函数
	 */
	protected abstract void onDraw();

	/**
	 * @param dbElapseTime_s
	 *                两周期之间间隔时间，秒为单位
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
	protected abstract void onClockCycle(double dbElapseTime_s,
			YSystem system, YScene sceneCurrent, YCamera camera,
			Matrix4 matrix4VP, Matrix4 matrix4Projection,
			Matrix4 matrix4View);

	/**
	 * 在渲染线程中被初始化时，回调该函数，您可以在此进行相应的开放图库（OpenGL）操作
	 * 
	 * @param system
	 *                引擎系统
	 * @param configurationGL
	 *                开放图库（OpenGL）配置信息
	 * @param iWidth
	 *                <b>渲染视图</b>{@link YView}
	 *                的宽度，像素为单位
	 * @param iHeight
	 *                <b>渲染视图</b>{@link YView}
	 *                的高度，像素为单位
	 */
	protected abstract void onGL_Initialize(YSystem system,
			YGL_Configuration configurationGL, int iWidth,
			int iHeight);
}
