package ygame.framework.domain;

//由系统建立、给予/收回该对象，客户不能控制
//系统负责保证交出的对象是内存完成的
public final class YWriteBundle
{
	// 换成原子引用会更好?
	// fei problem
	volatile YBundleHolder holder;

	YWriteBundle()
	{
	}

	/**
	 * 向包裹中写入“投影-视图-模型矩阵”
	 * 
	 * @param f_arrMVP_Matrix
	 *                要写入包裹的矩阵，浮点型数组，长度16
	 */
	public void writeMVP_Matrix(float[] f_arrMVP_Matrix)
	{
		if (null == holder.f_arrMVP_Matrix)
			holder.f_arrMVP_Matrix = new float[16];
		// src->des
		System.arraycopy(f_arrMVP_Matrix, 0, holder.f_arrMVP_Matrix, 0,
				16);
	}

	/**
	 * 向包裹中写入“视图-模型矩阵”
	 * 
	 * @param f_arrMV_Matrix
	 *                要写入包裹的矩阵，浮点型数组，长度16
	 */
	public void writeMV_Matrix(float[] f_arrMV_Matrix)
	{
		if (null == holder.f_arrMV_Matrix)
			holder.f_arrMV_Matrix = new float[16];
		System.arraycopy(f_arrMV_Matrix, 0, holder.f_arrMV_Matrix, 0,
				16);
	}

	/**
	 * 向包裹中写入“模型矩阵”
	 * 
	 * @param f_arrModelMatrix
	 *                要写入包裹的矩阵，浮点型数组，长度16
	 */
	public void writeModelMatrix(float[] f_arrModelMatrix)
	{
		if (null == holder.f_arrModelMatrix)
			holder.f_arrModelMatrix = new float[16];
		System.arraycopy(f_arrModelMatrix, 0, holder.f_arrModelMatrix,
				0, 16);
	}

	/**
	 * 向包裹中写入“纹理法向量矩阵”
	 * 
	 * @param f_arrNormalMatrix
	 *                要写入包裹的矩阵，浮点型数组，长度9
	 */
	public void writeNormalMatrix(float[] f_arrNormalMatrix)
	{
		if (null == holder.f_arrNormalMatrix)
			holder.f_arrNormalMatrix = new float[9];
		System.arraycopy(f_arrNormalMatrix, 0,
				holder.f_arrNormalMatrix, 0, 9);
	}

	/**
	 * 向包裹中写入浮点型数组
	 * 
	 * @param iDestinationSlotKey
	 *                包裹目的槽位的键值
	 * @param fSourceArray
	 *                要写入包裹的浮点型数组
	 */
	public void writeFloatArray(int iDestinationSlotKey,
			float[] fSourceArray)
	{
		float[] fDesArray = (float[]) holder.map
				.get(iDestinationSlotKey);
		if (null == fDesArray)
		{
			fDesArray = new float[fSourceArray.length];
			holder.map.put(iDestinationSlotKey, fDesArray);
		}
		System.arraycopy(fSourceArray, 0, fDesArray, 0,
				fSourceArray.length);
	}

}
