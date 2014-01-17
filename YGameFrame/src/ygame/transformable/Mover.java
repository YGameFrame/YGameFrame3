package ygame.transformable;

import ygame.math.YMatrix;
import ygame.math.vector.Vector3;

/**
 * <b>移动体实现</b>
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
public class Mover implements IMover
{
	private YMatrix matrix = new YMatrix();

	// private boolean bDirty;

	private Vector3 vector3Shaft = new Vector3(0, 0, 1);

	private float[] f_arrData = new float[]
	{ 0,// dirty,1为脏数据
			0, 0, 0, // x , y , z
			0, 0, 1, // rx , ry , rz
			0,// angle
	};

	private static final int DIRTY = 0;
	private static final int X = 1;
	private static final int Y = 2;
	private static final int Z = 3;
	private static final int RX = 4;
	private static final int RY = 5;
	private static final int RZ = 6;
	private static final int ANGLE = 7;

	public Mover()
	{
		matrix.identity();
	}

	/**
	 * 获取变换矩阵
	 * 
	 * @return 变换矩阵
	 */
	public YMatrix getMatrix()
	{
		update();
		return matrix;
	}

	/**
	 * 设置变换数据
	 * 
	 * @param floatValues
	 *                变换数据
	 */
	public void setData(float[] floatValues)
	{
		this.f_arrData = floatValues;
	}

	/**
	 * 转为变换数组
	 * 
	 * @return 变换数组
	 */
	public float[] toFloatValues()
	{
		return f_arrData;
	}

	private boolean update()
	{
		if (1 == f_arrData[DIRTY])
		{
			matrix.identity()
					.translate(f_arrData[X], f_arrData[Y],
							f_arrData[Z])
					.rotate(f_arrData[ANGLE],
							f_arrData[RX],
							f_arrData[RY],
							f_arrData[RZ]);
			f_arrData[DIRTY] = 0;
			return true;
		}
		return false;

	}

	@Override
	public float getZ()
	{
		return f_arrData[Z];
	}

	@Override
	public IMover setZ(float fZ)
	{
		if (f_arrData[Z] == fZ)
			return this;
		f_arrData[DIRTY] = 1;
		f_arrData[Z] = fZ;
		return this;
	}

	@Override
	public float getY()
	{
		return f_arrData[Y];
	}

	@Override
	public IMover setY(float fY)
	{
		if (f_arrData[Y] == fY)
			return this;
		f_arrData[DIRTY] = 1;
		f_arrData[Y] = fY;
		return this;
	}

	@Override
	public float getX()
	{
		return f_arrData[X];
	}

	@Override
	public IMover setX(float fX)
	{
		if (f_arrData[X] == fX)
			return this;
		f_arrData[DIRTY] = 1;
		f_arrData[X] = fX;
		return this;
	}

	@Override
	public float getAngle()
	{
		return f_arrData[ANGLE];
	}

	@Override
	public IMover setAngle(float angle)
	{
		if (f_arrData[ANGLE] == angle)
			return this;
		f_arrData[DIRTY] = 1;
		f_arrData[ANGLE] = angle;
		return this;
	}

	@Override
	public Vector3 getShaft()
	{
		vector3Shaft.x = f_arrData[RX];
		vector3Shaft.y = f_arrData[RY];
		vector3Shaft.z = f_arrData[RZ];
		return vector3Shaft;
	}

	@Override
	public IMover setShaft(Vector3 vector3Shaft)
	{
		if (this.vector3Shaft.equals(vector3Shaft))
			return this;
		f_arrData[DIRTY] = 1;
		this.vector3Shaft = vector3Shaft;
		f_arrData[RX] = vector3Shaft.x;
		f_arrData[RY] = vector3Shaft.y;
		f_arrData[RZ] = vector3Shaft.z;
		return this;
	}
}
