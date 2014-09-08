package ygame.extension.domain.sprite;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import ygame.common.YConstants.Orientation;
import ygame.domain.YADomainLogic;
import ygame.domain.YDomain;
import ygame.exception.YException;
import ygame.extension.primitives.YMirrorYSquare;
import ygame.extension.primitives.YSquare;
import ygame.extension.program.YTileProgram;
import ygame.framework.core.YRequest;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;
import ygame.skeleton.YSkeleton;
import ygame.state_machine.StateMachine;
import ygame.state_machine.builder.YStateMachineBuilder;
import ygame.texture.YTileSheet;
import ygame.transformable.YMover;

public abstract class YASpriteDomainLogic<D extends YDomain> extends
		YADomainLogic
{
	private static final float RAD2DEG = 180 / MathUtils.PI;
	private static final String TAG = YASpriteDomainLogic.class
			.getSimpleName();

	protected int iRowIndex;
	protected int iColumnIndex;
	protected boolean bLockOrientation;

	final private YTileSheet tileSheet;
	final private YSkeleton skeletonRight;
	final private YSkeleton skeletonLeft;
	final protected float fSkeletonSideLen;
	final protected YMover mover = new YMover();

	protected StateMachine<YIStateClocker, YRequest, YASpriteDomainLogic<?>> stateMachine;
	protected Body body;
	protected D domainContext;

	protected float fInitX_M;
	protected float fInitY_M;

	private World world;

	private Orientation orientation = Orientation.RIGHT;

	protected YASpriteDomainLogic(YTileSheet tileSheet,
			float fSkeletonSideLen, World world)
	{
		this.fSkeletonSideLen = fSkeletonSideLen;
		this.tileSheet = tileSheet;
		this.skeletonLeft = new YMirrorYSquare(fSkeletonSideLen, false,
				true);
		this.skeletonRight = new YSquare(fSkeletonSideLen, false, true);

		this.world = world;

		mover.setZ(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onAttach(YSystem system, YBaseDomain domainContext)
	{
		this.domainContext = (D) domainContext;
		super.onAttach(system, domainContext);
		// 设计状态机
		YStateMachineBuilder<YIStateClocker, YRequest, YASpriteDomainLogic<?>> builder = YStateMachineBuilder
				.create(YIStateClocker.class, YRequest.class);
		YIStateClocker stateInit = designStateMachine(builder);
		this.stateMachine = builder.buildTransitionTemplate()
				.newStateMachine(stateInit, this);

		// 设计刚体
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(fInitX_M, fInitY_M);
		this.body = world.createBody(bd);
		world = null;// 释放之
		body.setDomain(domainContext);
		body.setFixedRotation(true);
		designBody(body);
		// body.setUserData(domainContext.KEY);
	}

	/**
	 * 设计刚体模型
	 * 
	 * @param body
	 *                需要设计的刚体
	 */
	protected abstract void designBody(Body body);

	/**
	 * 设计状态机
	 * 
	 * @param builder
	 *                状态机构建器
	 * @return 状态机初始状态
	 */
	protected abstract YIStateClocker designStateMachine(
			YStateMachineBuilder<YIStateClocker, YRequest, YASpriteDomainLogic<?>> builder);

	/**
	 * 确认当前朝向，根据输入，实时更新朝向是左还是右，如果返回null表示默认朝向，即不更改当前朝向 </br>
	 * <b>目前只支持左右朝向</b>{@link Orientation#LEFT}和 {@link Orientation#RIGHT}
	 * 
	 */
	protected abstract Orientation updateCurrentOrientation();

	/************************** 处理系统回调 *******************************/
	@Override
	protected void onCycle(double dbElapseTime_s, YDomain domainContext,
			YWriteBundle bundle, YSystem system,
			YScene sceneCurrent, YMatrix matrix4pv,
			YMatrix matrix4Projection, YMatrix matrix4View)
	{
		// 0. 确认朝向
		if (!bLockOrientation)
		{
			Orientation ori = updateCurrentOrientation();
			orientation = null == ori ? orientation : ori;
		}
		YSkeleton skeletonCurrent = getSkeletonByOrientation(orientation);
		// 1.据状态机模型计算当前状态各值
		stateMachine.getCurrentState().onClock((float) dbElapseTime_s,
				this, system, sceneCurrent);
		// 2.据刚体模型计算形态各值
		Vec2 position = body.getPosition();
		mover.setX(position.x).setY(position.y)
				.setAngle(body.getAngle() * RAD2DEG);
		// 3.摄像机追踪角色
		sceneCurrent.getCurrentCamera().setX(position.x)
				.setY(position.y);
		// 4.将参数交予着色程序渲染
		YTileProgram.YAdapter adapter = (YTileProgram.YAdapter) domainContext
				.getParametersAdapter();
		adapter.paramFramePosition(iRowIndex, iColumnIndex)
				.paramFrameSheet(tileSheet)
				.paramMatrixPV(matrix4pv).paramMover(mover)
				.paramSkeleton(skeletonCurrent);

	}

	private YSkeleton getSkeletonByOrientation(Orientation orientation)
	{
		switch (orientation)
		{
		case LEFT:
			return skeletonLeft;
		case RIGHT:
			return skeletonRight;

		default:
			throw new YException("暂不支持朝向：" + orientation, TAG,
					"@yunzhongtianjing");
		}
	}

	// 将请求交付状态机模型处理
	@Override
	protected boolean onDealRequest(YRequest request, YSystem system,
			YScene sceneCurrent, YBaseDomain domainContext)
	{
		return stateMachine.inputRequest(request);
	}

}
