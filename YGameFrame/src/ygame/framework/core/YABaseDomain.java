package ygame.framework.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import ygame.framework.core.YSystem.YAStateMachineContext;
import ygame.math.YMatrix;

/**
 * <b>抽象基础实体</b>
 * 
 * <p>
 * <b>概述</b>： 提供给系统关于实体的调用界面
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
	public final String KEY;

	private YSystem system;

	private boolean bAttached;

	private ConcurrentLinkedQueue<YRequest> penddingReqs = new ConcurrentLinkedQueue<YRequest>();

	/**
	 * @param KEY
	 *                参阅{@link #KEY}
	 */
	public YABaseDomain(String KEY)
	{
		this.KEY = KEY;
	}

	@Override
	final boolean onReceiveRequest(YRequest request)
	{
		return onReceiveRequest(request, system,
				system.getCurrentScene());
	}

	// 实体被添入场景时，被调用
	void attach(YSystem system)
	{
		if (bAttached)
			return;
		bAttached = true;
		this.system = system;

		YRequest req = null;
		while (null != (req = penddingReqs.poll()))
			sendRequest(req);

		onAttach(system);
	}

	/**
	 * 当实体被关联到系统时该函数被回调
	 * 
	 * @param system
	 *                系统
	 */
	// 此时yview已经有大小！！！
	protected void onAttach(YSystem system)
	{
	}

	/**
	 * 当实体被添入相应场景时该函数被回调
	 * 
	 * @param scene
	 *                实体被添入的的场景
	 */
	protected void onEnterScene(YScene scene)
	{
	}

	/**
	 * 每帧开始绘制前回调此函数，即在此时只有逻辑线程运行，绘图线程完成任务处于等待状态， 在此之后逻辑线程与绘图线程开始并发运行
	 * ，为了保证线程安全，它们之间不可再有任何交互！您可以通过复写该方法，在逻辑线程与 绘图线程“分道扬镳”之前做一些设置。
	 * 
	 */
	protected abstract void onPreframe();

	/**
	 * 绘图时回调此函数
	 * 
	 * @param system
	 *                系统
	 */
	protected abstract void onDraw(YSystem system);

	/**
	 * 当实体接受到该请求时，该函数被回调
	 * 
	 * @param request
	 *                请求
	 * @param system
	 *                系统
	 * @param sceneCurrent
	 *                实体当前所处的场景
	 * @return 真为处理了该请求，反之忽略该请求
	 */
	protected abstract boolean onReceiveRequest(YRequest request,
			YSystem system, YScene sceneCurrent);

	/**
	 * @param dbElapseTime_s
	 *                两周期之间间隔时间，秒为单位
	 * @param system
	 *                系统
	 * @param sceneCurrent
	 *                当前场景
	 * @param matrix4PV
	 *                “投影-视图矩阵”
	 * @param matrix4Projection
	 *                “投影矩阵”
	 * @param matrix4View
	 *                “视图矩阵”
	 */
	protected abstract void onClockCycle(double dbElapseTime_s,
			YSystem system, YScene sceneCurrent, YMatrix matrix4PV,
			YMatrix matrix4Projection, YMatrix matrix4View);

	/**
	 * 在渲染线程中被初始化时回调该函数，您可以在此进行相应的开放图库（OpenGL）操作
	 * 
	 * @param system
	 *                引擎系统
	 * @param configurationGL
	 *                开放图库（OpenGL）配置信息
	 * @param iWidth
	 *                <b>渲染视图</b>{@link YView} 的宽度，像素为单位
	 * @param iHeight
	 *                <b>渲染视图</b>{@link YView} 的高度，像素为单位
	 */
	protected abstract void onGL_Initialize(YSystem system,
			YGL_Configuration configurationGL, int iWidth,
			int iHeight);

	/**
	 * 向实体对象发送请求
	 * 
	 * @param request
	 *                发送的请求
	 */
	public final void sendRequest(YRequest request)
	{
		// TODO thread may be unsafe
		if (null == system)
		{
			penddingReqs.add(request);
			return;
		}
		sendRequest(request, system);
	}
}
