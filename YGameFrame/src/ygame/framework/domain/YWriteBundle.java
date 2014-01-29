package ygame.framework.domain;

//由系统建立、给予/收回该对象，客户不能控制
//系统负责保证交出的对象是内存完成的
public final class YWriteBundle
{
	// 换成原子引用会更好?
	// fei problem
	// XXX
	volatile Object[] map;

	YWriteBundle()
	{
	}

	/**
	 * 向包裹中写入浮点型数组
	 * 
	 * @param destinationSlotKey
	 *                写值的槽位
	 * @param fSourceArray
	 *                要写入包裹的浮点型数组
	 */
	public void writeFloatArray(int destinationSlotKey, float[] fSourceArray)
	{
		float[] fDesArray = (float[]) map[destinationSlotKey];
		if (null == fDesArray)
		{
			fDesArray = new float[fSourceArray.length];
			map[destinationSlotKey] = fDesArray;
		}
		System.arraycopy(fSourceArray, 0, fDesArray, 0,
				fSourceArray.length);
	}

	/**
	 * 向包裹中写入布尔值
	 * 
	 * @param destinationSlotKey
	 *                写值的槽位
	 * @param bSourceBoolean
	 *                要写入包裹的布尔值
	 */
	public void writeBoolean(int destinationSlotKey, boolean bSourceBoolean)
	{
		map[destinationSlotKey] = bSourceBoolean;
	}

	/**
	 * 向包裹中写入对象</br>
	 * <b>注：</b>利用该方法写入的对象务必保证其线程不变性
	 * （即该对象在任何线程都不应该被随意更改状态）
	 * 
	 * @param destinationSlotKey
	 *                写值的槽位
	 * @param objectSrc
	 *                要写入包裹的对象
	 */
	public void writeObject(int destinationSlotKey, Object objectSrc)
	{
		map[destinationSlotKey] = objectSrc;
	}
}
