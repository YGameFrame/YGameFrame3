package ygame.framework.domain;

import java.util.Map;

import ygame.framework.core.YSystem;

//由系统建立、给予/收回该对象，客户不能控制其创建、回收
//系统保证交出的对象是内存完整的
/**
 * <b>可读包裹</b>
 * 
 * <p>
 * <b>概述</b>： 视图从该包裹中读取逻辑传来的绘图信息，从而完成绘制。
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： 您可以复写<b>实体逻辑</b>的方法——
 * {@link YADomainLogic#onClockCycle(double, YBaseDomain, YWriteBundle, YSystem, YScene, YCamera, Matrix4, Matrix4, Matrix4)}
 * ， 向其中的参数<b>可写包裹</b>{@link YWriteBundle}
 * 写入相应绘图信息，本对象即可收到这些信息。
 * 
 * <p>
 * <b>注</b>： 您不应该保存该对象的引用，该对象由系统创建并管理。
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public final class YReadBundle
{
	// 换成原子引用会更好?
	// fei problem
	// XXX
	volatile Map<Integer, Object> map;

	YReadBundle()
	{
	}

	/**
	 * 从包裹中读取指定槽位的浮点型数组
	 * 
	 * @param iSlotKey
	 *                浮点型数组的所处槽位
	 * @return 指定槽位的浮点型数组
	 */
	public float[] readFloatArray(int iSlotKey)
	{
		return (float[]) map.get(iSlotKey);
	}

	/**
	 * 从包裹中读取制定槽位的布尔值
	 * 
	 * @param iSlotKey
	 *                布尔值所处槽位
	 * @return 指定槽位的布尔值
	 */
	public boolean readBoolean(int iSlotKey)
	{
		return (Boolean) map.get(iSlotKey);
	}

}
