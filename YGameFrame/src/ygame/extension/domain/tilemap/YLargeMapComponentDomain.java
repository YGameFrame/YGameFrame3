package ygame.extension.domain.tilemap;

import ygame.domain.YADomainLogic;
import ygame.domain.YDomain;
import ygame.domain.YDomainView;
import ygame.framework.core.YGL_Configuration;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.request.YRequest;
import ygame.math.YMatrix;
import android.annotation.SuppressLint;

class YLargeMapComponentDomain extends YDomain
{

	public YLargeMapComponentDomain(String KEY, YADomainLogic logic,
			YDomainView view)
	{
		super(KEY, logic, view);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onPreframe()
	{
		super.onPreframe();
	}

	@Override
	@SuppressLint("WrongCall")
	protected void onDraw(YSystem system)
	{
		super.onDraw(system);
	}

	@Override
	protected void onGL_Initialize(YSystem system,
			YGL_Configuration configurationGL, int iWidth,
			int iHeight)
	{
		super.onGL_Initialize(system, configurationGL, iWidth, iHeight);
	}

	@Override
	protected void onClockCycle(double dbElapseTime_s, YSystem system,
			YScene sceneCurrent, YMatrix matrix4pv,
			YMatrix matrix4Projection, YMatrix matrix4View)
	{
		super.onClockCycle(dbElapseTime_s, system, sceneCurrent,
				matrix4pv, matrix4Projection, matrix4View);
	}

	@Override
	protected boolean onReceiveRequest(YRequest request, YSystem system,
			YScene sceneCurrent)
	{
		return super.onReceiveRequest(request, system, sceneCurrent);
	}
}
