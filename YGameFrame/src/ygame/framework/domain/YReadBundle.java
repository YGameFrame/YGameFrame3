package ygame.framework.domain;

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
	volatile YBundleHolder holder;

	YReadBundle()
	{
	}

	/**
	 * 从包裹中读取“投影-视图-模型矩阵”
	 * 
	 * @return “投影-视图-模型矩阵”，浮点型数组
	 */
	public float[] readMVP_Matrix()
	{
		return holder.f_arrMVP_Matrix;
	}

	/**
	 * 从包裹中读取“视图-模型矩阵”
	 * 
	 * @return “视图-模型矩阵”，浮点型数组
	 */
	public float[] readMV_Matrix()
	{
		return holder.f_arrMV_Matrix;
	}

	/**
	 * 从包裹中读取“模型矩阵”
	 * 
	 * @return “模型矩阵”，浮点型数组
	 */
	public float[] readModelMatrix()
	{
		return holder.f_arrModelMatrix;
	}

	/**
	 * 从包裹中读取“纹理法向量矩阵”
	 * 
	 * @return “纹理法向量矩阵”，浮点型数组
	 */
	public float[] readNormalMatrix()
	{
		return holder.f_arrNormalMatrix;
	}

	/**
	 * 从包裹中读取指定槽位的浮点型数组
	 * 
	 * @param iSlotKey
	 *                要读取的浮点型数组的槽位
	 * @return 指定槽位的浮点型数组
	 */
	public float[] readFloatArray(int iSlotKey)
	{
		return (float[]) holder.map.get(iSlotKey);
	}

}
