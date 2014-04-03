package ygame.framework.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ygame.framework.request.YRequest;
import ygame.framework.request.YRequest.YWhen;
import ygame.math.YMatrix;
import android.annotation.SuppressLint;

/**
 * <b>集群实体</b>
 * 
 * <p>
 * <b>概述</b>： 由若干个实体构成的集群。
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
public class YClusterDomain extends YABaseDomain
{
	private List<YABaseDomain> domains = new LinkedList<YABaseDomain>();

	public YClusterDomain(String KEY)
	{
		super(KEY);
	}

	@Override
	protected void onPreframe()
	{
		for (YABaseDomain domain : domains)
			domain.onPreframe();
	}

	@SuppressLint("WrongCall")
	@Override
	protected void onDraw(YSystem system)
	{
		for (YABaseDomain domain : domains)
			domain.onDraw(system);
	}

	@Override
	protected boolean onReceiveRequest(YRequest request, YSystem system,
			YScene sceneCurrent)
	{
		switch (request.iKEY)
		{
		case YClusterDomainRequest.iADD_COMPONENTS:
			this.domains.addAll(((YClusterDomainRequest) request).domains);
			return true;

		default:
			break;
		}
		return false;
	}

	public void addComponentDomains(Collection<YABaseDomain> domains,
			YSystem system)
	{
		YClusterDomainRequest request = new YClusterDomainRequest(
				YClusterDomainRequest.iADD_COMPONENTS, this,
				YWhen.BEFORE_RENDER);
		request.domains = domains;
		system.inputRequest(request);
	}

	@Override
	protected void onClockCycle(double dbElapseTime_s, YSystem system,
			YScene sceneCurrent, YMatrix matrix4pv,
			YMatrix matrix4Projection, YMatrix matrix4View)
	{
		for (YABaseDomain domain : domains)
			domain.onClockCycle(dbElapseTime_s, system,
					sceneCurrent, matrix4pv,
					matrix4Projection, matrix4View);
	}

	@Override
	protected void onGL_Initialize(YSystem system,
			YGL_Configuration configurationGL, int iWidth,
			int iHeight)
	{
		for (YABaseDomain domain : domains)
			domain.onGL_Initialize(system, configurationGL, iWidth,
					iHeight);
	}

	private static class YClusterDomainRequest extends YDomainRequest
	{
		public YClusterDomainRequest(int KEY, YABaseDomain WHO,
				YWhen when)
		{
			super(KEY, WHO, when);
		}

		private static final int iADD_COMPONENTS = 0;

		private Collection<YABaseDomain> domains;

		
	}

}
