package ygame.extension.domain;

import ygame.domain.YADomainLogic;
import ygame.domain.YDomain;
import ygame.domain.YDomainView;
import ygame.extension.program.YSimpleProgram;
import ygame.extension.program.YSimpleProgram.YAdapter;
import ygame.framework.core.YRequest;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YReadBundle;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;
import ygame.skeleton.YSkeleton;
import ygame.transformable.YMover;
import android.content.res.Resources;
import android.opengl.GLES20;

public class YPolyLineDomain extends YDomain
{
	private static final int REQ_SHOW = 0;

	private static final int max_vertex_num = 3;
	
	public YPolyLineDomain(String KEY, Resources resources)
	{
		super(KEY, new YPolyLineLogic(), new YDomainView(
				YSimpleProgram.getInstance(resources))
		{
			@Override
			protected void onDraw(YReadBundle bundle,
					YBaseDomain domainContext,
					YSystem system)
			{
				GLES20.glLineWidth(10);
				super.onDraw(bundle, domainContext, system);
				GLES20.glLineWidth(1);
			}
		});
		((YDomainView)view).iDrawMode = GLES20.GL_LINE_STRIP;
	}

	public void show(float... positionCoords)
	{
		YPolyLineReq req = new YPolyLineReq(REQ_SHOW);
		req.posCoords = positionCoords;
		sendRequest(req);
	}

	private static class YPolyLineLogic extends YADomainLogic
	{
		private YPolyLineSkeleton skeleton = new YPolyLineSkeleton();
		private YMover mover = new YMover();

		@Override
		protected void onCycle(double dbElapseTime_s,
				YDomain domainContext, YWriteBundle bundle,
				YSystem system, YScene sceneCurrent,
				YMatrix matrix4pv, YMatrix matrix4Projection,
				YMatrix matrix4View)
		{
			YSimpleProgram.YAdapter adapter = (YAdapter) domainContext
					.getParametersAdapter();
			adapter.paramMatrixPV(matrix4pv).paramMover(mover)
					.paramSkeleton(skeleton);
		}

		@Override
		protected boolean onDealRequest(YRequest request,
				YSystem system, YScene sceneCurrent,
				YBaseDomain domainContext)
		{
			YPolyLineReq req = (YPolyLineReq) request;
			switch (req.iKEY)
			{
			case REQ_SHOW:
				skeleton.setPositions(req.posCoords);
				return true;

			default:
				return false;
			}
		}
	}

	private static class YPolyLineReq extends YRequest
	{
		private float[] posCoords;

		public YPolyLineReq(int iKEY)
		{
			super(iKEY, YWhen.BEFORE_RENDER);
		}
	}

	private static class YPolyLineSkeleton extends YSkeleton
	{
		private YPolyLineSkeleton()
		{
			setPositions(new float[]{
					-1, 0, 0,// 左点
					1, 0, 0, // 右点
					0, 1, 0 // 上点
			});
			setColors(createRandomColorData(max_vertex_num));
		}

		private void setPositions(float[] positions)
		{
			setPositions(positions, false);
		}
	}

}
