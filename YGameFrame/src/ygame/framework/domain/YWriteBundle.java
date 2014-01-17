package ygame.framework.domain;

import java.util.Map;

//由系统建立、给予/收回该对象，客户不能控制
//系统负责保证交出的对象是内存完成的
public final class YWriteBundle
{
	// 换成原子引用会更好?
	// fei problem
	// XXX
	volatile Map<Integer, Object> map;

	YWriteBundle()
	{
	}

	/**
	 * 向包裹中写入浮点型数组
	 * 
	 * @param iDestinationSlotKey
	 *                写值的槽位
	 * @param fSourceArray
	 *                要写入包裹的浮点型数组
	 */
	public void writeFloatArray(int iDestinationSlotKey,
			float[] fSourceArray)
	{
		float[] fDesArray = (float[]) map.get(iDestinationSlotKey);
		if (null == fDesArray)
		{
			fDesArray = new float[fSourceArray.length];
			map.put(iDestinationSlotKey, fDesArray);
		}
		System.arraycopy(fSourceArray, 0, fDesArray, 0,
				fSourceArray.length);
	}

	/**
	 * 向包裹中写入布尔值
	 * 
	 * @param iDestinationSlotKey
	 *                写值的槽位
	 * @param bSourceBoolean
	 *                要写入包裹的布尔值
	 */
	public void writeBoolean(int iDestinationSlotKey, boolean bSourceBoolean)
	{
		map.put(iDestinationSlotKey, bSourceBoolean);
	}

}
