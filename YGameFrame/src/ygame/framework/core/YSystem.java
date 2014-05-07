package ygame.framework.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.opengles.GL10;

import ygame.exception.YException;
import ygame.framework.YIResultCallback;
import ygame.framework.core.YRequest.YWhen;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;

/**
 * <b>系统</b>
 * 
 * <p>
 * <b>概述</b>： 管理<b>渲染视图</b>{@link YView}渲染、逻辑调度等
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>： </br>1.关于该系统的启动参见{@link #start()}；
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public final class YSystem
{
	private static final String strTAG = "YSystem";

	private static final int iCOMMA = Integer.MIN_VALUE;
	private static final YRequest requestCOMMA_BeforeRendering = new YRequest(
			iCOMMA, YWhen.BEFORE_RENDER);
	private static final YRequest requestCOMMA_Rendering_LogicThread = new YRequest(
			iCOMMA, YWhen.RENDERING_EXE_IN_LOGIC_THREAD);
	private final YIConcurrentQueue<YRequest> queueRendering = new YConcurrentQueue<YRequest>();
	private final YIConcurrentQueue<YRequest> queueBeforeRendering = new YConcurrentQueue<YRequest>();

	private final List<YScene> scenes = new ArrayList<YScene>();
	/** 绝不能被设置为空，任何时候必有一场景，如果被设置为null，则被视为设置为缺损场景。 */
	private YScene sceneCurrent;

	public final YView YVIEW;
	private boolean bGLInitialized;
	private double dbLastClockTime_ms;
	private ScheduledExecutorService sesTimer;
	private double dbFrameRate_fps;

	private CyclicBarrier barrier = new CyclicBarrier(2);

	private Context context;

	private YGL_Configuration configurationGL;

	// 测试帧速率
	private int mFrameCount;
	private long mStartTime;
	private double mLastMeasuredFPS;

	final private YScene sceneDEFAULT;
	private static final boolean bDEBUG = true;

	public static final String GLThreadName = "GLThread";

	private boolean bSceneLocked;

	public boolean bFPS_Debug;

	YSystem(YView yview)
	{
		YVIEW = yview;

		YScene.reloadStateMachineModel();

		dbFrameRate_fps = getRefreshRate(YVIEW.getContext());
		context = YVIEW.getContext();

		sceneDEFAULT = new YScene(this, "DEFAULT");
		scenes.add(sceneDEFAULT);
		sceneCurrent = sceneDEFAULT;
		sceneCurrent.forceRun();
	}

	/**
	 * 获取上下文
	 * 
	 * @return 上下文
	 */
	public Context getContext()
	{
		return context;
	}

	/**
	 * 启动系统</br> 1.若<b>渲染视图</b>{@link YView} 表面尚未创建，该系统无法启动；</br>
	 * 2.该系统会在<b>渲染视图</b>创建 完成后自动启动一次。
	 */
	public void start()
	{
		System.out.println("系统启动");
		inputRequest(requestCOMMA_BeforeRendering);
		if (!bGLInitialized)
			return;
		dbLastClockTime_ms = SystemClock.elapsedRealtime();

		if (sesTimer != null)
			return;

		sesTimer = Executors.newScheduledThreadPool(1);
		sesTimer.scheduleAtFixedRate(new YClockTask(), 0,
				(long) (1000 / dbFrameRate_fps),
				TimeUnit.MILLISECONDS);

		// bStart
	}

	/**
	 * 停止系统
	 * 
	 * @return 停止成功返回真，反之假
	 */
	public boolean stop()
	{
		System.out.println("系统停止");
		if (sesTimer != null)
		{
			sesTimer.shutdownNow();
			sesTimer = null;
			return true;
		}
		return false;
	}

	/**
	 * 设置渲染的帧速率
	 * 
	 * @param dbFrameRate_fps
	 *                单位：帧每秒
	 */
	public void setFrameRate(double dbFrameRate_fps)
	{
		if (dbFrameRate_fps < 0.5)
		{
			Log.w(strTAG, "设置的帧速率过小，设置将不生效");
			return;
		}

		this.dbFrameRate_fps = dbFrameRate_fps;
		if (stop())
			// 以新速率重启
			start();
	}

	/**
	 * 获取当前场景
	 * 
	 * @return 当前场景
	 */
	public YScene getCurrentScene()
	{
		return sceneCurrent;
	}

	/**
	 * 切换场景，如果您需要得知场景是否切换成功，参见{@link #switchScene(YScene, YIResultCallback)}
	 * 
	 * @param sceneTo
	 *                场景
	 */
	public void switchScene(YScene sceneTo)
	{
		switchScene(sceneTo, null);
	}

	/**
	 * 切换场景<br>
	 * <b>注：</b>切换场景时一个异步过程，不会立即返回，如果您需要知道切换的结果，可以通过传入一个<b>回调</b>
	 * {@link YIResultCallback}，通过将
	 * {@link YIResultCallback#onResultReceived(Object)}
	 * 中的参数objResult强制类型转为Boolean对象得到切换结果：真为切换成功，反之失败。<br>
	 * <b>示例：</b>
	 * 
	 * <pre class="prettyprint">
	 * system.switchScene(mainScene, new YICallback()
	 * {
	 * 	public void run(Object objResult)
	 * 	{
	 * 		if((Boolean)objResult)
	 * 			//如果场景切换成功，如此这般……
	 * 		else
	 * 			//如果失败，如此这般……
	 * 	}
	 * });
	 * </pre>
	 * 
	 * @param sceneTo
	 *                场景
	 * @param switchResultCallBack
	 *                切换结果回调
	 */
	public void switchScene(YScene sceneTo,
			YIResultCallback switchResultCallBack)
	{
		YSystemRequest request = new YSystemRequest(
				YSystemRequest.iSWITCH_SCENE,
				YWhen.BEFORE_RENDER);
		request.scene = sceneTo;
		request.callback = switchResultCallBack;
		inputRequest(request);
	}

	private void handleSwitchScene(final YScene sceneNext,
			final YIResultCallback switchResultCallBack)
	{// 切换前检查系统是否正在执行场景切换，正在则返回、否则切换之
		if (bSceneLocked)
		{
			if (null != switchResultCallBack)
				switchResultCallBack.onResultReceived(false);
			return;
		}
		bSceneLocked = true;

		sceneCurrent.requestQuit(new YIResultCallback()
		{
			public void onResultReceived(Object objResult)
			{
				if ((Boolean) objResult)
				{// 该时机为：退出动作完成（即退出状态结束）进入卸载状态，时钟开始之初、绘图线程停滞，
					// 逻辑线程作每帧绘前设置之前。
					// sceneCurrent = sceneNext;
					sceneNext.requestEnter(new YIResultCallback()
					{
						public void onResultReceived(
								Object objResult)
						{
							if (null != switchResultCallBack)
								switchResultCallBack
										.onResultReceived(objResult);
							bSceneLocked = false;
						}
					});
				} else
				{
					if (null != switchResultCallBack)
						switchResultCallBack
								.onResultReceived(false);
					bSceneLocked = false;
				}
			}
		});
	}

	/**
	 * 强制将当前场景设置为指定场景 <br>
	 * <b>注：</b><li>当前场景将会直接进入“卸载状态”，而新场景则直接进入“运行状态” <li>该方法是异步的，不是立即执行！<li>
	 * 如果您传入空值null的话，则将场景改为系统默认场景
	 * 
	 * @param scene
	 *                要设置的场景
	 */
	public void forceSetCurrentScene(YScene scene)
	{
		YSystemRequest request = new YSystemRequest(
				YSystemRequest.iFORCE_SET_SCENE,
				YWhen.BEFORE_RENDER);
		request.scene = scene;
		inputRequest(request);
		// this.sceneCurrent = scene;
	}

	private void handleForceSetCurrentScene(YScene scene)
	{
		if (scene == sceneCurrent)
		{
			sceneCurrent.forceRun();
			return;
		}

		// 1.卸载当前场景
		// 强行改变当前场景状态为卸载，
		// 并执行“将卸载”回调、回调时通知了系统当前场景已改变为null
		sceneCurrent.forceUnmount();

		// 2.将当前场景置为要替换的场景
		if (null == scene)
			sceneCurrent = sceneDEFAULT;
		else
			sceneCurrent = scene;

		// 3.添入新场景
		// 强行改变新场景状态为运行，
		// 并执行“将运行”回调
		sceneCurrent.forceRun();
	}

	private boolean dealSystemRequest(YSystemRequest request)
	{
		switch (request.iKEY)
		{
		case YSystemRequest.iSWITCH_SCENE:
			handleSwitchScene(request.scene, request.callback);
			break;

		case YSystemRequest.iFORCE_SET_SCENE:
			handleForceSetCurrentScene(request.scene);
			break;

		default:
			break;
		}
		return true;
	}

	private void clockCycle()
	{
		double dbCurrentClockCycle_ms = SystemClock.elapsedRealtime();
		final double dbDeltaTime_s = (dbCurrentClockCycle_ms - dbLastClockTime_ms) / 1000.0;
		dbLastClockTime_ms = dbCurrentClockCycle_ms;
		// 通知下属
		sceneCurrent.onClockCycle(dbDeltaTime_s);
		if (bDEBUG)
			calculateFPS();
	}

	private void calculateFPS()
	{
		++mFrameCount;
		if (mFrameCount % 50 == 0)
		{
			long now = System.nanoTime();
			double elapsedS = (now - mStartTime) / 1.0e9;
			double msPerFrame = (1000 * elapsedS / mFrameCount);
			mLastMeasuredFPS = 1000 / msPerFrame;

			mFrameCount = 0;
			mStartTime = now;
			if (bFPS_Debug)
				System.out.println("fps=" + mLastMeasuredFPS);
		}
	}

	private void preFrame()
	{
		sceneCurrent.onPreframe();
	}

	private void notifyClockCycle()
	{
		// 轮询渲染前队列
		pollRequestQueue(queueBeforeRendering);

		// 交换内存块
		preFrame();

		// 通知视图渲染
		YVIEW.requestRender();

		// 轮询渲染时队列
		pollRequestQueue(queueRendering);

		// 通知逻辑周期到来
		clockCycle();

		// 向队列发送分隔符
		inputRequest(requestCOMMA_BeforeRendering);
		inputRequest(requestCOMMA_Rendering_LogicThread);

	}

	private void pollRequestQueue(YIConcurrentQueue<YRequest> queue)
	{
		for (;;)
		{
			YRequest request = queue.dequeue();
			if (null == request)
				return;
			if (iCOMMA == request.iKEY)
				return;
			if (request instanceof YSystemRequest)
			{
				dealSystemRequest((YSystemRequest) request);
				return;
			}

			if (null == request.target)
				throw new YException("系统受到没有接受目标的请求！",
						getClass().getName(), "错误");
			request.target.onReceiveRequest(request);
			// if (request instanceof YSystemRequest)
			// dealRequest((YSystemRequest) request);
			// else
			// sceneCurrent.onReceiveRequest(request);
		}
	}

	void notifyGL_Ready(GL10 gl10, int iWidth, int iHeight)
	{
		GLES20.glViewport(0, 0, iWidth, iHeight);

		configurationGL = YGL_Configuration.getInstanceInGL();

		dbLastClockTime_ms = SystemClock.elapsedRealtime();

		for (int i = 0; i < 2; i++)
		{
			preFrame();
			clockCycle();
		}

		sceneCurrent.onGL_Initialize(configurationGL, iWidth, iHeight);

		bGLInitialized = true;
		start();
	}

	@SuppressLint("WrongCall")
	void notifyGL_Draw()
	{
		try
		{
			sceneCurrent.preDraw();
			sceneCurrent.onDraw();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				barrier.await();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			} catch (BrokenBarrierException e)
			{
				e.printStackTrace();
			}
		}
	}

	void notifyCurrentSceneChanged(YScene scene)
	{
		if (scene == sceneCurrent)
			return;
		sceneCurrent.forceUnmount();

		if (null == scene)
			this.sceneCurrent = sceneDEFAULT;
		else
			this.sceneCurrent = scene;
	}

	/**
	 * 向系统输入请求
	 * 
	 * @param request
	 *                请求
	 * @return 真表示系统接收该请求，反之该请求被忽略
	 * 
	 * @see ygame.framework.core.YAStateMachineContext#dealSystemRequest(ygame.framework.core.YRequest)
	 */
	private boolean inputRequest(YRequest request)
	{
		switch (request.WHEN_TO_DEAL)
		{
		case BEFORE_RENDER:
			return queueBeforeRendering.enqueue(request);

		case RENDERING_EXE_IN_LOGIC_THREAD:
			return queueRendering.enqueue(request);

		default:
			return false;
		}
	}

	/**
	 * 判断手机是否支持OpenGL_ES2.0
	 * 
	 * @param context
	 *                上下文
	 * @return 支持返回真，否则假
	 */
	public static boolean isSupportGL2_0(Context context)
	{
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo configurationInfo = activityManager
				.getDeviceConfigurationInfo();
		boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		return supportsEs2;
	}

	/**
	 * 获取系统刷新速率
	 * 
	 * @param context
	 *                上下文
	 * @return 系统帧速率
	 */
	public static double getRefreshRate(Context context)
	{
		return ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getRefreshRate();
	}

	private class YClockTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				notifyClockCycle();
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				try
				{
					barrier.await();
				} catch (InterruptedException e1)
				{
					e1.printStackTrace();
				} catch (BrokenBarrierException e1)
				{
					e1.printStackTrace();
				}
			}
		}

	}

	private static class YSystemRequest extends YRequest
	{
		private static final int iSWITCH_SCENE = 0;
		private static final int iFORCE_SET_SCENE = 1;

		private YScene scene;
		private YIResultCallback callback;

		private YSystemRequest(int iKEY, YWhen when)
		{
			super(iKEY, when);
		}
	}

	/**
	 * <b>状态机上下文</b>
	 * 
	 * <p>
	 * <b>概述</b>： TODO
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
	static abstract class YAStateMachineContext
	{
		/**
		 * 向状态机输入<b>请求</b>{@link YRequest}
		 * 
		 * @param request
		 *                请求
		 * @return 真为接收请求，反之拒绝
		 */
		// 由系统分发请求时输入，客户不可干涉
		abstract boolean onReceiveRequest(YRequest request);

		void sendRequest(YRequest request, YSystem system)
		{
			request.target = this;
			system.inputRequest(request);
		}
		// abstract protected void
		// onClockCycle(double dbElapseTime_s);
	}

}
