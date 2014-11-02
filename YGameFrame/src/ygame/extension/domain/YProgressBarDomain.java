package ygame.extension.domain;

import ygame.domain.YDomainView;
import ygame.extension.primitives.YRectangle;
import ygame.framework.R;
import ygame.framework.core.YRequest;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YABaseDomainLogic;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YReadBundle;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;
import ygame.program.YAShaderProgram;
import ygame.skeleton.YSkeleton;
import ygame.transformable.YMover;
import ygame.utils.YTextFileUtils;
import android.content.res.Resources;

public final class YProgressBarDomain extends YBaseDomain
{
	/***************** 不可重复 start*******************/
	private static final int PVM = 0;
	private static final int PRO = 1;
	/***************** 不可重复 end*******************/

	/***************** 不可重复 start*******************/
	private static final int REQ_SET_PROGRESS = 0;
	private static final int REQ_SET_POSITION = 1;
	/***************** 不可重复 end*******************/

	private static YProgressBarProg program;

	public YProgressBarDomain(String KEY, Resources resources,
			float fWidth, float fHeight)
	{
		super(
				KEY,
				new YProgressBarLogic(),
				//@formatter:off
				new YDomainView(
						null == program 
						? program = new YProgressBarProg(resources , fWidth , fHeight)
						: program));
				//@formatter:on
		
		// init(Color.GREEN , Color.RED);
	}

	// public void init(int backgroundColor , int foregroundColor)
	// {
	//
	// }

	public void setProgress(float fProgress)
	{
		fProgress = fProgress > 1.0f ? 1 : fProgress;
		fProgress = fProgress < 0 ? 0 : fProgress;

		YProgressBarReq req = new YProgressBarReq(REQ_SET_PROGRESS);
		req.fProgress = fProgress;
		sendRequest(req);
	}

	public void setPosition(float fX, float fY, float fZ)
	{
		YProgressBarReq req = new YProgressBarReq(REQ_SET_POSITION);
		req.pos = new float[]
		{ fX, fY, fZ };
		sendRequest(req);
	}

	private static class YProgressBarLogic extends YABaseDomainLogic
	{

		private YMatrix matrixPVM = new YMatrix();
		private YMover mover = new YMover();

		private float[] fProgress = new float[1];

		@Override
		protected boolean onDealRequest(YRequest request,
				YSystem system, YScene sceneCurrent,
				YBaseDomain domainContext)
		{
			YProgressBarReq req = (YProgressBarReq) request;
			switch (request.iKEY)
			{
			case REQ_SET_PROGRESS:
				fProgress[0] = req.fProgress;
				return true;

			case REQ_SET_POSITION:
				mover.setX(req.pos[0]).setY(req.pos[1])
						.setZ(req.pos[2]);
				return true;

			default:
				return false;
			}
		}

		@Override
		protected void onClockCycle(double dbElapseTime_s,
				YBaseDomain domainContext, YWriteBundle bundle,
				YSystem system, YScene sceneCurrent,
				YMatrix matrix4pv, YMatrix matrix4Projection,
				YMatrix matrix4View)
		{
			YMatrix.multiplyMM(matrixPVM, matrix4pv,
					mover.getMatrix());
			bundle.writeFloatArray(PVM, matrixPVM.toFloatValues());
			bundle.writeFloatArray(PRO, fProgress);
		}
	}

	private static class YProgressBarProg extends YAShaderProgram
	{
		private YSkeleton skeleton;

		private YProgressBarProg(Resources resources, float fWidth,
				float fHeight)
		{
			fWidth = fWidth > 0 ? fWidth : 0;
			fHeight = fHeight > 0 ? fHeight : 0;
			skeleton = new YRectangle(fWidth, fHeight, false, true);
			fillCodeAndParam(YTextFileUtils.getStringFromResRaw(
					R.raw.texture_vsh, resources),
					YTextFileUtils.getStringFromResRaw(
							R.raw.progress_bar,
							resources), null);
		}

		@Override
		protected void applyParams(int iProgramHandle,
				YReadBundle bundle, YSystem system,
				YDomainView domainView)
		{
			setUniformf("progress", bundle.readFloatArray(PRO)[0]);
			setUniformMatrix("uPVMMatrix",
					bundle.readFloatArray(PVM));

			setAttribute("aPosition",
					skeleton.getPositionDataSource());
			setAttribute("aTexCoord",
					skeleton.getTexCoordDataSource());

			if (skeleton.hasIBO())
				drawWithIBO(skeleton.getIndexHandle(),
						skeleton.getVertexNum(),
						domainView.iDrawMode);
			else
				drawWithVBO(skeleton.getVertexNum(),
						domainView.iDrawMode);
		}

	}

	private static class YProgressBarReq extends YRequest
	{
		private float fProgress;
		private float[] pos;

		public YProgressBarReq(int iKEY)
		{
			super(iKEY);
		}
	}
}
