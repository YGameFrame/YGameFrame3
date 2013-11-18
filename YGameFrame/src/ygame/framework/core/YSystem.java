package ygame.framework.core;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.opengles.GL10;

import ygame.framework.YAStateMachineContext;
import ygame.framework.request.YRequest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;

//文档
//TODO 
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

	public YABaseDomain domainTest;

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

	private final YView YVIEW;
	private boolean bGLInitialized;
	private double dbLastClockTime_ms;
	private ScheduledExecutorService sesTimer;
	private double dbFrameRate_fps;

	private boolean bSide;

	private CyclicBarrier barrier = new CyclicBarrier(2);

	private Context context;

	private YGL_Configuration configurationGL;

	YSystem(YView yview)
	{
		YVIEW = yview;
		dbFrameRate_fps = getRefreshRate(YVIEW.getContext());
		context = YVIEW.getContext();
	}

	public Context getContext()
	{
		return context;
	}

	/**
	 * 启动系统</br> 1.若<b>渲染视图</b>{@link YView}
	 * 表面尚未创建，该系统无法启动；</br> 2.该系统会在<b>渲染视图</b>创建
	 * 完成后自动启动一次。
	 */
	public void start()
	{
		System.out.println("系统启动");
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

	private YCamera cameraTest = new YCamera();

	private void clockCycle()
	{
		double dbCurrentClockCycle_ms = SystemClock.elapsedRealtime();
		final double dbDeltaTime_s = (dbCurrentClockCycle_ms - dbLastClockTime_ms) / 1000.0;
		dbLastClockTime_ms = dbCurrentClockCycle_ms;
		// 通知下属
		// TODO
		domainTest.onClockCycle(dbDeltaTime_s, this, null, cameraTest,
				null, null, null);
	}

	private void swap()
	{
		// TODO
		bSide = !bSide;
		domainTest.onSwap(bSide);
	}

	private void notifyClockCycle()
	{
		// 轮询队列
		// TODO
		// pollRequestQueue();

		// // 交换内存块
		swap();

		// 通知视图渲染
		YVIEW.requestRender();

		// 通知逻辑周期到来
		clockCycle();

		// 队列发送分隔符
		// TODO
		// launchRequest(requestCOMMA);

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

	void notifyGL_Ready(GL10 gl10, int iWidth, int iHeight)
	{
		// fei test
		cameraTest.setProjectionMatrix(iWidth, iHeight);
		cameraTest.setZ(6);

		configurationGL = YGL_Configuration.getInstanceInGL();

		for (int i = 0; i < 2; i++)
		{
			swap();
			clockCycle();
		}

		domainTest.onGL_Initialize(this, configurationGL, iWidth,
				iHeight);

		bGLInitialized = true;
		start();
	}

	@SuppressLint("WrongCall")
	void notifyGL_Draw()
	{
		// TODO Auto-generated method stub
		domainTest.onDraw();

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
		// TODO Auto-generated method stub

	}

	private class YClockTask implements Runnable
	{
		@Override
		public void run()
		{
			notifyClockCycle();
		}

	}

}
