package ygame.framework.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ygame.exception.YException;
import ygame.framework.YIResultCallback;
import ygame.framework.core.YRequest.YWhen;
import ygame.framework.core.YSystem.YAStateMachineContext;
import ygame.math.YMatrix;
import ygame.state_machine.StateMachine;
import ygame.state_machine.YIAction;
import ygame.state_machine.YStateMachineTemplate;
import ygame.state_machine.builder.YStateMachineBuilder;
import ygame.utils.YLog;
import android.annotation.SuppressLint;
import android.opengl.GLES20;

/**
 * <b>场景</b>
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
public class YScene extends YAStateMachineContext
{
	private static YStateMachineTemplate<YSceneState, YSceneRequest, YScene> stateMachineModel;

	final private String strName;

	@SuppressLint("UseSparseArrays")
	protected final Map<String, YABaseDomain> mapDomains = new HashMap<String, YABaseDomain>();

	private YCamera camera = new YCamera();
	private final YMatrix matrixPV = new YMatrix();

	protected final YSystem SYSTEM;

	private StateMachine<YSceneState, YSceneRequest, YScene> stateMachine;

	private boolean bGLInit;

	private List<YIResultCallback> callBackWhenEntereds = new ArrayList<YIResultCallback>();
	private List<YIResultCallback> callBackWhenQuiteds = new ArrayList<YIResultCallback>();
	private List<YIResultCallback> callBackWhenRunneds = new ArrayList<YIResultCallback>();
	private List<YIResultCallback> callBackWhenUnmounteds = new ArrayList<YIResultCallback>();

	private List<YISceneClockerPlugin> clockerPlugins = new ArrayList<YISceneClockerPlugin>();

	public YScene(YSystem system, String strName)
	{
		this.SYSTEM = system;
		this.strName = strName;
		stateMachine = getStateMachineModel(SYSTEM).newStateMachine(
				YSceneState.UNMOUNT, this);
	}

	@Override
	protected boolean onReceiveRequest(YRequest request)
	{
		return dealRequest((YSceneRequest) request);
	}

	private boolean dealRequest(YSceneRequest request)
	{
		switch (request.iKEY)
		{
		case YSceneRequest.KEY.iADD_DOMAINS:
			handleAddDomains(request.domains);
			return true;

		case YSceneRequest.KEY.iREMOVE_DOMAINS:
			handleRemoveDomains(request.domains, request.domainKeys);
			return true;

		case YSceneRequest.KEY.iTO_QUIT:
			return handleStateTransition(request,
					callBackWhenQuiteds);
		case YSceneRequest.KEY.iTO_ENTER:
			return handleStateTransition(request,
					callBackWhenEntereds);
		case YSceneRequest.KEY.iTO_RUN:
			return handleStateTransition(request,
					callBackWhenRunneds);
		case YSceneRequest.KEY.iTO_UNMOUNT:
			return handleStateTransition(request,
					callBackWhenUnmounteds);

		default:
			return false;
		}
	}

	/**
	 * 向场景添加实体
	 * 
	 * @param domains
	 *                实体（可多个）
	 */
	public void addDomains(YABaseDomain... domains)
	{
		YSceneRequest request = new YSceneRequest(
				YSceneRequest.KEY.iADD_DOMAINS,
				YWhen.BEFORE_RENDER);
		request.domains = domains;
		sendRequest(request, SYSTEM);
	}

	private void handleAddDomains(YABaseDomain[] domains)
	{
		for (YABaseDomain domain : domains)
		{
			if (mapDomains.containsKey(domain.KEY))
				throw new YException("重复添加实体或实体键冲突", getClass()
						.getSimpleName(),
						"1.不同实体应该拥有不同的键；2.同一实体不应多次被添加");
			mapDomains.put(domain.KEY, domain);
			domain.attach(SYSTEM);
			domain.onEnterScene(this);
		}
	}

	/**
	 * 从场景中移除实体
	 * 
	 * @param domains
	 *                需要移除的实体（可多个）
	 */
	public void removeDomains(YABaseDomain... domains)
	{
		YSceneRequest request = new YSceneRequest(
				YSceneRequest.KEY.iREMOVE_DOMAINS,
				YWhen.BEFORE_RENDER);
		request.domains = domains;
		sendRequest(request, SYSTEM);
	}

	/**
	 * 从场景中移除实体
	 * 
	 * @param domainKEYs
	 *                需要移除的实体的键值（可多个）
	 */
	public void removeDomains(String... domainKEYs)
	{
		// YABaseDomain[] domains = new YABaseDomain[domainKEYs.length];
		// for (int i = 0; i < domainKEYs.length; i++)
		// domains[i] = mapDomains.get(domainKEYs[i]);
		//
		// removeDomains(domains);
		YSceneRequest request = new YSceneRequest(
				YSceneRequest.KEY.iREMOVE_DOMAINS,
				YWhen.BEFORE_RENDER);
		request.domainKeys = domainKEYs;
		sendRequest(request, SYSTEM);
	}

	private void handleRemoveDomains(YABaseDomain[] domains,
			String[] domainKeys)
	{
		if (null != domains)
			for (YABaseDomain domain : domains)
				mapDomains.remove(domain.KEY);

		if (null != domainKeys)
			for (String key : domainKeys)
				mapDomains.remove(key);
	}

	/**
	 * 获取当前摄像机
	 * 
	 * @return 场景当前使用的摄像机
	 */
	public YCamera getCurrentCamera()
	{
		return camera;
	}

	/**
	 * 获取场景当前所处<b>状态</b>{@link YSceneState}
	 * 
	 * @return 当前状态
	 */
	public YSceneState getCurrentState()
	{
		return stateMachine.getCurrentState();
	}

	protected void onClockCycle(double dbDeltaTime_s)
	{
		YMatrix.multiplyMM(matrixPV, camera.getProjectMatrix(),
				camera.getViewMatrix());
		Collection<YABaseDomain> domains = mapDomains.values();
		for (YABaseDomain domain : domains)
			domain.onClockCycle(dbDeltaTime_s, SYSTEM, this,
					matrixPV, camera.getProjectMatrix(),
					camera.getViewMatrix());

		for (YISceneClockerPlugin plugin : clockerPlugins)
			plugin.onClock(dbDeltaTime_s);
	}

	public final void addClockerPlugin(
			YISceneClockerPlugin... clockerPlugin)
	{
		for (YISceneClockerPlugin plugin : clockerPlugin)
			this.clockerPlugins.add(plugin);
	}

	/**
	 * 在该场景中查找指定实体对象
	 * 
	 * @param domainKey
	 *                实体键
	 * @return 查找成功返回实体引用，查找失败返回空值
	 */
	public final YABaseDomain queryDomainByKey(String domainKey)
	{
		return mapDomains.get(domainKey);
	}

	protected void onPreframe()
	{
		Collection<YABaseDomain> domains = mapDomains.values();
		for (YABaseDomain domain : domains)
			domain.onPreframe();
	}

	protected void onGL_Initialize(YGL_Configuration configurationGL,
			int iWidth, int iHeight)
	{
		if (bGLInit)
			return;
		camera.setProjectionMatrix(iWidth, iHeight);
		Collection<YABaseDomain> domains = mapDomains.values();
		for (YABaseDomain domain : domains)
			domain.onGL_Initialize(SYSTEM, configurationGL, iWidth,
					iHeight);
		bGLInit = true;
	}

	/**
	 * 可在此处理场景每次绘制前的设置
	 */
	protected void preDraw()
	{
		if (!bGLInit)
			onGL_Initialize(YGL_Configuration.getInstanceInGL(),
					SYSTEM.YVIEW.getWidth(),
					SYSTEM.YVIEW.getHeight());
		int clearMask = GLES20.GL_COLOR_BUFFER_BIT;
		clearMask |= GLES20.GL_DEPTH_BUFFER_BIT;
		GLES20.glClear(clearMask);
	}

	@SuppressLint("WrongCall")
	protected void onDraw()
	{
		Collection<YABaseDomain> domains = mapDomains.values();
		for (YABaseDomain domain : domains)
		{
			try
			{
				domain.onDraw(SYSTEM);
			} catch (Exception e)
			{
				if (e instanceof YException)
					throw new YException((YException) e);
				e.printStackTrace();
			}
		}
	}

	private static class YSceneRequest extends YRequest
	{

		private static final class KEY
		{
			private static final int iADD_DOMAINS = 1;
			private static final int iREMOVE_DOMAINS = 2;
			//
			private static final int iTO_ENTER = 3;
			private static final int iTO_RUN = 4;
			private static final int iTO_QUIT = 5;
			private static final int iTO_UNMOUNT = 6;
		}

		private YABaseDomain[] domains;
		private String[] domainKeys;
		private YIResultCallback callback;

		private YSceneRequest(int iKEY, YWhen when)
		{
			super(iKEY, when);
		}
	}

	/**
	 * <b>场景状态</b>
	 * 
	 * <p>
	 * <b>概述</b>： 场景有四种状态：
	 * <li>{@link YSceneState#UNMOUNT}；
	 * <li>{@link YSceneState#ENTERING}；
	 * <li>{@link YSceneState#RUN}；
	 * <li>{@link YSceneState#QUITING}；
	 * 
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
	public enum YSceneState
	{
		/** <b>正在退出</b> */
		QUITING("正在退出"),
		/** <b>正在进入</b> */
		ENTERING("正在进入"),
		/** <b>运行</b> */
		RUN("运行"),
		/** <b>卸载</b> */
		UNMOUNT("卸载");

		private String strName;

		private YSceneState(String strName)
		{
			this.strName = strName;
		}

		@Override
		public String toString()
		{
			return strName;
		}
	}

	// XXX 暂且解决安卓静态变量不回收问题
	static void reloadStateMachineModel()
	{
		stateMachineModel = null;
	}

	// 单例，所有场景的状态机模型一样，皆为卸载->启动->运行->退出->卸载，故无需重复之
	private static YStateMachineTemplate<YSceneState, YSceneRequest, YScene> getStateMachineModel(
			YSystem SYSTEM)
	{
		if (null == stateMachineModel)
			synchronized (YScene.class)
			{
				if (null == stateMachineModel)
					stateMachineModel = designStateMachineModel(SYSTEM);
			}
		return stateMachineModel;
	}

	private static YStateMachineTemplate<YSceneState, YSceneRequest, YScene> designStateMachineModel(
			final YSystem SYSTEM)
	{
		final YSceneRequest requestTO_ENTER = new YSceneRequest(
				YSceneRequest.KEY.iTO_ENTER,
				YWhen.BEFORE_RENDER);
		final YSceneRequest requestTO_RUN = new YSceneRequest(
				YSceneRequest.KEY.iTO_RUN, YWhen.BEFORE_RENDER);
		final YSceneRequest requestTO_QUIT = new YSceneRequest(
				YSceneRequest.KEY.iTO_QUIT, YWhen.BEFORE_RENDER);
		final YSceneRequest requestTO_UNMOUNT = new YSceneRequest(
				YSceneRequest.KEY.iTO_UNMOUNT,
				YWhen.BEFORE_RENDER);

		YStateMachineBuilder<YSceneState, YSceneRequest, YScene> builder = YStateMachineBuilder
				.create(YSceneState.class, YSceneRequest.class);
		builder.newTransition().from(YSceneState.ENTERING)
				.to(YSceneState.RUN).on(requestTO_RUN);
		builder.newTransition().from(YSceneState.RUN)
				.to(YSceneState.QUITING).on(requestTO_QUIT);
		builder.newTransition().from(YSceneState.QUITING)
				.to(YSceneState.UNMOUNT).on(requestTO_UNMOUNT);
		builder.newTransition().from(YSceneState.UNMOUNT)
				.to(YSceneState.ENTERING).on(requestTO_ENTER);

		builder.onEntry(YSceneState.ENTERING)
				.perform(new YIAction<YScene.YSceneState, YScene.YSceneRequest, YScene>()
				{

					@Override
					public void onTransition(
							YSceneState from,
							YSceneState to,
							YSceneRequest causedBy,
							YScene context,
							StateMachine<YSceneState, YSceneRequest, YScene> stateMachine)
					{
						SYSTEM.notifyCurrentSceneChanged(context);
						context.willEnter();
					}
				});

		builder.onEntry(YSceneState.RUN)
				.perform(new YIAction<YScene.YSceneState, YScene.YSceneRequest, YScene>()
				{

					@Override
					public void onTransition(
							YSceneState from,
							YSceneState to,
							YSceneRequest causedBy,
							YScene context,
							StateMachine<YSceneState, YSceneRequest, YScene> stateMachine)
					{
						// SYSTEM.notifyCurrentSceneChanged(context);
						context.willRun();
					}
				});

		builder.onEntry(YSceneState.QUITING)
				.perform(new YIAction<YScene.YSceneState, YScene.YSceneRequest, YScene>()
				{

					@Override
					public void onTransition(
							YSceneState from,
							YSceneState to,
							YSceneRequest causedBy,
							YScene context,
							StateMachine<YSceneState, YSceneRequest, YScene> stateMachine)
					{
						// SYSTEM.notifyCurrentSceneChanged(context);
						context.willQuit();
					}
				});

		builder.onEntry(YSceneState.UNMOUNT)
				.perform(new YIAction<YScene.YSceneState, YScene.YSceneRequest, YScene>()
				{

					@Override
					public void onTransition(
							YSceneState from,
							YSceneState to,
							YSceneRequest causedBy,
							YScene context,
							StateMachine<YSceneState, YSceneRequest, YScene> stateMachine)
					{
						context.willUnmount();
						SYSTEM.notifyCurrentSceneChanged(null);
					}
				});
		return builder.buildTransitionTemplate();
	}

	void forceUnmount()
	{
		stateMachine.forceSetState(YSceneState.UNMOUNT);
	}

	void forceRun()
	{
		stateMachine.forceSetState(YSceneState.RUN);
	}

	protected void willUnmount()
	{
		for (YIResultCallback callback : callBackWhenQuiteds)
			callback.onResultReceived(true);
		callBackWhenQuiteds.clear();
		YLog.i(getClass().getName(), "场景" + this + "将卸载");
	}

	protected void willQuit()
	{
		for (YIResultCallback callback : callBackWhenRunneds)
			callback.onResultReceived(true);
		callBackWhenRunneds.clear();
		YLog.i(getClass().getName(), "场景" + this + "将退出");
	}

	protected void willRun()
	{
		for (YIResultCallback callback : callBackWhenEntereds)
			callback.onResultReceived(true);
		callBackWhenEntereds.clear();
		YLog.i(getClass().getName(), "场景" + this + "将运行");
	}

	protected void willEnter()
	{
		for (YIResultCallback callback : callBackWhenUnmounteds)
			callback.onResultReceived(true);
		callBackWhenUnmounteds.clear();
		YLog.i(getClass().getName(), "场景" + this + "将进入");
	}

	/**
	 * 请求场景退出，<b>注意场景退出并不是立即地，它可能要花费一定时间去处理该任务（如播放动画）</b>，因此如果您关注场景对此请求的处理情况
	 * ， 可以传入一个回调接口，具体地， 该接口将异步地返回boolean值来指示处理情况，即
	 * {@link YIResultCallback#onResultReceived(Object)}
	 * 中参数objResult，您可以将之强制类型转为Boolean类型：如果为真，则表示此时场景退出完成；反之退出失败。
	 * 
	 * @param callBackWhenQuited
	 *                退出完成情况之回调
	 */
	public final void requestQuit(YIResultCallback callBackWhenQuited)
	{
		YSceneRequest request = new YSceneRequest(
				YSceneRequest.KEY.iTO_QUIT, YWhen.BEFORE_RENDER);
		request.callback = callBackWhenQuited;
		sendRequest(request, SYSTEM);
	}

	public final void requestEnter(YIResultCallback callBackWhenEntered)
	{
		YSceneRequest request = new YSceneRequest(
				YSceneRequest.KEY.iTO_ENTER,
				YWhen.BEFORE_RENDER);
		request.callback = callBackWhenEntered;
		sendRequest(request, SYSTEM);
	}

	public final void requestUnmount(YIResultCallback callBackWhenUnmounted)
	{
		YSceneRequest request = new YSceneRequest(
				YSceneRequest.KEY.iTO_UNMOUNT,
				YWhen.BEFORE_RENDER);
		request.callback = callBackWhenUnmounted;
		sendRequest(request, SYSTEM);
	}

	public final void requestRun(YIResultCallback callBackWhenRunned)
	{
		YSceneRequest request = new YSceneRequest(
				YSceneRequest.KEY.iTO_RUN, YWhen.BEFORE_RENDER);
		request.callback = callBackWhenRunned;
		sendRequest(request, SYSTEM);
	}

	private boolean handleStateTransition(YSceneRequest request,
			List<YIResultCallback> callbacks)
	{
		boolean bRes = stateMachine.inputRequest(request);
		if (!bRes)
		{// 状态机未接收请求，对其调用者异步告知转入“对应状态”失败
			if (null != request.callback)
				request.callback.onResultReceived(false);
		} else
		{// 状态机接收请求，状态跳转成功，暂存回调，待“对应状态”结束后回调之
			if (null != request.callback)
				callbacks.add(request.callback);
		}
		return bRes;
	}

	@Override
	public String toString()
	{
		return strName + ":" + super.toString();
	}

}
