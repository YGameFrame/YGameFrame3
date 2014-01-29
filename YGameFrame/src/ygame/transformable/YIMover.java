package ygame.transformable;

import ygame.math.vector.Vector3;

/**
 * <b>移动子</b>
 * 
 * <p>
 * <b>概述</b>：设置或读取移动体的位置信息
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
public interface YIMover extends YIMoverGetter
{

	/**
	 * 设置位置竖坐标
	 * 
	 * @param fZ
	 *                竖坐标
	 * @return 移动者
	 */
	public abstract YIMover setZ(float fZ);

	/**
	 * 设置位置纵坐标
	 * 
	 * @param fY
	 *                纵坐标
	 * @return 移动者
	 */
	public abstract YIMover setY(float fY);

	/**
	 * 设置位置横坐标
	 * 
	 * @param fX
	 *                横坐标
	 * @return 移动者
	 */
	public abstract YIMover setX(float fX);

	/**
	 * 设置旋转角度
	 * 
	 * @param angle
	 *                旋转角度
	 * @return 移动者
	 */
	public abstract YIMover setAngle(float angle);

	/**
	 * 设置转轴
	 * 
	 * @param vector3Shaft
	 *                转轴
	 * @return 移动者
	 */
	public abstract YIMover setShaft(Vector3 vector3Shaft);

}