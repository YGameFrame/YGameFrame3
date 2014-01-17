package ygame.framework.core;

import ygame.framework.YAStateMachineContext;
import ygame.math.YMatrix;

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
	 * 每帧开始绘制前回调此函数，即在此时只有逻辑线程运行，绘图线程完成任务处于等待状态，
	 * 在此之后逻辑线程与绘图线程开始并发运行
	 * ，为了保证线程安全，它们之间不可再有任何交互！您可以通过复写该方法，在逻辑线程与
	 * 绘图线程“分道扬镳”之前做一些设置。
	 * 
	 */
	protected abstract void onPreframe();

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
			YMatrix matrix4VP, YMatrix matrix4Projection,
			YMatrix matrix4View);

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
