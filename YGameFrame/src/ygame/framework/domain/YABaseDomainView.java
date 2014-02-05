package ygame.framework.domain;

import ygame.framework.core.YGL_Configuration;
import ygame.framework.core.YSystem;
import android.annotation.SuppressLint;

/**
 * <b>抽象基础实体视图</b>
 * 
 * <p>
 * <b>概述</b>：分工渲染画面
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
public abstract class YABaseDomainView
{
	final YReadBundle bundle = new YReadBundle();

	/**
	 * 实体每帧绘制时该函数被回调
	 * 
	 * @param domainContext
	 *                实体上下文
	 * @param system
	 *                系统
	 */
	@SuppressLint("WrongCall")
	void onDraw(YBaseDomain domainContext, YSystem system)
	{
		onDraw(bundle, domainContext, system);
	}

	/**
	 * 从包裹中取出必要信息绘图
	 * 
	 * @param bundle
	 *                可读包裹
	 * @param domainContext
	 *                所隶属的实体，实体上下文，可类型转换为所属实体类型
	 * @param system
	 *                系统
	 */
	protected abstract void onDraw(YReadBundle bundle,
			YBaseDomain domainContext, YSystem system);

	/**
	 * 初始化时被调用
	 * 
	 * @param system
	 *                引擎系统
	 * @param configurationGL
	 *                开放图库（ OpenGL）的配置清单
	 * @param iWidth
	 *                渲染视图宽度
	 * @param iHeight
	 *                渲染视图高度
	 */
	protected abstract void onGL_Initialize(YSystem system,
			YGL_Configuration configurationGL, int iWidth,
			int iHeight);

	/**
	 * 参见{@link YBaseDomain#onPreframe()}
	 * 
	 * @param domainContext
	 *                实体上下文
	 */
	protected void onPreframe(YBaseDomain domainContext)
	{
	}

}
