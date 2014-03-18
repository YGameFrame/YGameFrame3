package ygame.framework.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.opengles.GL10;

import ygame.framework.YAStateMachineContext;
import ygame.framework.request.YRequest;
import ygame.framework.request.YRequest.YWhen;
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
public final class YSystem extends YAStateMachineContext
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
	private static final boolean bDEBUG = true;

	public static final String GLThreadName = "GLThread";

	YSystem(YView yview)
	{
		YVIEW = yview;
		dbFrameRate_fps = getRefreshRate(YVIEW.getContext());
		context = YVIEW.getContext();

		YScene sceneDefault = new YScene(this);
		scenes.add(sceneDefault);
		sceneCurrent = sceneDefault;
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

	//XXX
	public void testSetScene(YScene scene)
	{
		this.sceneCurrent = scene;
		scenes.add(scene);
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

		// // 交换内存块
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
				dealRequest((YSystemRequest) request);
			else
				sceneCurrent.inputRequest(request);
		}
	}

	private void dealRequest(YSystemRequest requestDeal)
	{
		// TODO Auto-generated method stub

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

	/**
	 * 向系统输入请求
	 * 
	 * @param request
	 *                请求
	 * 
	 * @see ygame.framework.YAStateMachineContext#inputRequest(ygame.framework.request.YRequest)
	 */
	@Override
	public void inputRequest(YRequest request)
	{
		switch (request.WHEN)
		{
		case BEFORE_RENDER:
			queueBeforeRendering.enqueue(request);
			break;

		case RENDERING_EXE_IN_LOGIC_THREAD:
			queueRendering.enqueue(request);
			break;

		default:
			break;
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
		public YSystemRequest(int iKEY, YWhen when)
		{
			super(iKEY, when);
		}
	}

}
