package ygame.domain;

import ygame.domain.YABaseShaderProgram.YABaseParametersAdapter;
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

	/**
	 * @param KEY
	 *                实体标识
	 * @param logic
	 *                控制实体的逻辑
	 * @param view
	 *                渲染实体的视图
	 */
	public YDomain(String KEY, YADomainLogic logic, YDomainView view)
	{
		super(KEY, logic, view);
		this.view = view;
	}

	/**
	 * 获取<b>渲染程序</b>的<b>参数适配器</b>
	 * {@link YABaseParametersAdapter}</br>
	 * 注：您应该对获得的<b>参数适配器</b>根据当前使用的<b>具体渲染程序</b>进行类型转换
	 * ，并进而填写之
	 * 
	 * @return 参数适配器
	 */
	public final YABaseParametersAdapter getParametersAdapter()
	{
		return view.program.daParamsAdapter;
	}

}
