package ygame.transformable;

import ygame.math.vector.Vector3;

/**
 * <b>移动定位体</b>
 * 
 * <p>
 * <b>概述</b>：向外界公布自己的位置信息
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
public interface YIMoverGetter
{

	/**
	 * 移动体所处位置竖坐标
	 * 
	 * @return 竖坐标
	 */
	float getZ();

	/**
	 * 移动体所处位置纵坐标
	 * 
	 * @return 纵坐标
	 */
	float getY();

	/**
	 * 移动体所处位置横坐标
	 * 
	 * @return 横坐标
	 */
	float getX();

	/**
	 * 移动体旋转角度
	 * 
	 * @return 旋转角度
	 */
	float getAngle();

	/**
	 * 移动体转轴
	 * 
	 * @return 转轴
	 */
	Vector3 getShaft();

}