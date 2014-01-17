package ygame.framework.domain;

import ygame.framework.core.YGL_Configuration;
import ygame.framework.core.YSystem;
import android.annotation.SuppressLint;

public abstract class YADomainView
{
	final YReadBundle bundle = new YReadBundle();

	@SuppressLint("WrongCall")
	void onDraw(YBaseDomain domainContext)
	{
		onDraw(bundle, domainContext);
	}

	/**
	 * 从包裹中取出必要信息绘图
	 * 
	 * @param bundle
	 *                可读包裹
	 * @param domainContext
	 *                所隶属的实体，实体上下文，可类型转换为所属实体类型
	 */
	protected abstract void onDraw(YReadBundle bundle,
			YBaseDomain domainContext);

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
