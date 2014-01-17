package ygame.domain;

import ygame.domain.YAShaderProgram.YAParametersAdapter;
import ygame.framework.domain.YBaseDomain;

/**
 * <b>实体</b>
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
public class YDomain extends YBaseDomain
{

	private YDomainView view;

	public YDomain(int iKEY, YDomainLogic logic, YDomainView view)
	{
		super(iKEY, logic, view);
		this.view = view;
	}

	/**
	 * 获取<b>参数适配器</b>{@link YAParametersAdapter}
	 * 
	 * @return 参数适配器
	 */
	public final YAParametersAdapter getParametersAdapter()
	{
		return view.program.daParamsAdapter;
	}

}
