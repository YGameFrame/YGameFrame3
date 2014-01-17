package ygame.framework.core;

import ygame.math.YMatrix;
import ygame.math.vector.Vector3;
import ygame.transformable.IMover;

/**
 * <b>摄像机</b>
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
public class YCamera implements IMover
{
	private YMatrix matrixProj = new YMatrix();
	private float left;
	private float right;
	private float bottom;
	private float top;
	private float near = 1;
	private float far = 30;
	private boolean bDirtyProj;

	private YMatrix matrixView = new YMatrix();
	private float eyeX = 0;
	private float eyeY = 0;
	private float eyeZ = 3;
	private float targetX = 0;
	private float targetY = 0;
	private float targetZ = 0;
	private float upX = 0;
	private float upY = 1;
	private float upZ = 0;
	private boolean bDirtyView = false;
	private float angle = 0;
	private Vector3 vector3Shaft = new Vector3(0, 0, 1);

	public YCamera()
	{
		matrixView.setLookAtM(eyeX, eyeY, eyeZ, targetX, targetY,
				targetZ, upX, upY, upZ);
	}

	/**
	 * 设置投影矩阵
	 * 
	 * @param iWidth
	 *                视口宽度
	 * @param iHeight
	 *                视口高度
	 */
	public void setProjectionMatrix(int iWidth, int iHeight)
	{
		bDirtyProj = true;
		left = -1;
		right = 1;
		bottom = -iHeight / (float) iWidth;
		top = iHeight / (float) iWidth;
	}

	/**
	 * 获取视图矩阵
	 * 
	 * @return 视图矩阵
	 */
	public YMatrix getViewMatrix()
	{
		update();
		return matrixView;
	}

	/**
	 * 获取投影矩阵
	 * 
	 * @return 投影矩阵
	 */
	public YMatrix getProjectMatrix()
	{
		update();
		return matrixProj;
	}

	private boolean update()
	{
		boolean bRes = false;
		if (bDirtyProj)
		{
			matrixProj.frustum(left, right, bottom, top, near, far);
			bDirtyProj = false;
			bRes = true;
		}

		if (bDirtyView)
		{
			matrixView.setLookAtM(eyeX, eyeY, eyeZ, targetX,
					targetY, targetZ, upX, upY, upZ)
					.rotate(angle, vector3Shaft.x,
							vector3Shaft.y,
							vector3Shaft.z);
			bDirtyView = false;
			bRes = true;
		}

		return bRes;
	}

	public float getAngle()
	{
		return angle;
	}

	public YCamera setAngle(float angle)
	{
		if (this.angle == angle)
			return this;
		bDirtyView = true;
		this.angle = angle;
		return this;
	}

	public Vector3 getShaft()
	{
		return vector3Shaft;
	}

	public YCamera setShaft(Vector3 vector3Shaft)
	{
		if (this.vector3Shaft.equals(vector3Shaft))
			return this;
		bDirtyView = true;
		this.vector3Shaft = vector3Shaft;
		return this;
	}

	public float getX()
	{
		return eyeX;
	}

	public float getY()
	{
		return eyeY;
	}

	public float getZ()
	{
		return eyeZ;
	}

	public YCamera setX(float fX)
	{
		if (this.eyeX == fX)
			return this;
		bDirtyView = true;
		eyeX = fX;
		return this;
	}

	public YCamera setY(float fY)
	{
		if (this.eyeY == fY)
			return this;
		bDirtyView = true;
		eyeY = fY;
		return this;
	}

	public YCamera setZ(float fZ)
	{
		if (this.eyeZ == fZ)
			return this;
		bDirtyView = true;
		eyeZ = fZ;
		return this;
	}
}