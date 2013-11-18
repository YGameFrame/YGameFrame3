package ygame.math;

import ygame.math.vector.Vector3;

//放在逻辑层
/**
 * <b>变换子</b>
 * 
 * <p>
 * <b>概述</b>：产生各种变换矩阵
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
public abstract class YATransformer
{
	protected final Vector3 mPosition, mRotation, mScale;
	protected final Quaternion mOrientation;
	protected final Quaternion mTmpOrientation;
	protected Vector3 mRotationAxis;
	protected boolean mRotationDirty;
	protected Vector3 mLookAt;
	protected final Vector3 mTmpAxis, mTmpVec;
	protected boolean mIsCamera, mQuatWasSet;
	protected final Vector3 mTmpRotX = new Vector3();
	protected final Vector3 mTmpRotY = new Vector3();
	protected final Vector3 mTmpRotZ = new Vector3();
	protected final double[] mLookAtMatrix = new double[16];

	public YATransformer()
	{
		mPosition = new Vector3();
		mRotation = new Vector3();
		mScale = new Vector3(1, 1, 1);
		mOrientation = new Quaternion();
		mTmpOrientation = new Quaternion();
		mTmpAxis = new Vector3();
		mTmpVec = new Vector3();
		mRotationDirty = true;
	}

	public void setPosition(Vector3 position)
	{
		mPosition.setAll(position);
	}

	public void setPosition(double x, double y, double z)
	{
		mPosition.setAll(x, y, z);
	}

	public Vector3 getPosition()
	{
		return mPosition;
	}

	public void setX(double x)
	{
		mPosition.x = x;
	}

	public double getX()
	{
		return mPosition.x;
	}

	public void setY(double y)
	{
		mPosition.y = y;
	}

	public double getY()
	{
		return mPosition.y;
	}

	public void setZ(double z)
	{
		mPosition.z = z;
	}

	public double getZ()
	{
		return mPosition.z;
	}

	public void setOrientation()
	{
		if (!mRotationDirty && mLookAt == null)
			return;

		mOrientation.identity();
		if (mLookAt != null)
		{
			mTmpRotZ.setAll(mLookAt).subtract(mPosition)
					.normalize();

			if (mTmpRotZ.isZero())
				mTmpRotZ.z = 1;

			mTmpRotX.setAll(Vector3.Y).cross(mTmpRotZ).normalize();

			if (mTmpRotX.isZero())
			{
				mTmpRotZ.x += .0001f;
				mTmpRotX.cross(mTmpRotZ).normalize();
			}

			mTmpRotY.setAll(mTmpRotZ);
			mTmpRotY.cross(mTmpRotX);

			Matrix.setIdentityM(mLookAtMatrix, 0);
			mLookAtMatrix[Matrix4.M00] = mTmpRotX.x;
			mLookAtMatrix[Matrix4.M10] = mTmpRotX.y;
			mLookAtMatrix[Matrix4.M20] = mTmpRotX.z;
			mLookAtMatrix[Matrix4.M01] = mTmpRotY.x;
			mLookAtMatrix[Matrix4.M11] = mTmpRotY.y;
			mLookAtMatrix[Matrix4.M21] = mTmpRotY.z;
			mLookAtMatrix[Matrix4.M02] = mTmpRotZ.x;
			mLookAtMatrix[Matrix4.M12] = mTmpRotZ.y;
			mLookAtMatrix[Matrix4.M22] = mTmpRotZ.z;

			// TODO: This will be fixed
			// by Issue #968
			mOrientation.fromRotationMatrix(mLookAtMatrix);
		} else
		{
			mOrientation.multiply(mTmpOrientation.fromAngleAxis(
					Vector3.Y, mRotation.y));
			mOrientation.multiply(mTmpOrientation.fromAngleAxis(
					Vector3.Z, mRotation.z));
			mOrientation.multiply(mTmpOrientation.fromAngleAxis(
					Vector3.X, mRotation.x));
			if (mIsCamera)
				mOrientation.inverse();
		}
		//
		// //TODO: This may cause problems
	}

	public void rotateAround(Vector3 axis, double angle)
	{
		rotateAround(axis, angle, true);
	}

	public void rotateAround(Vector3 axis, double angle, boolean append)
	{
		if (append)
		{
			mTmpOrientation.fromAngleAxis(axis, angle);
			mOrientation.multiply(mTmpOrientation);
		} else
		{
			mOrientation.fromAngleAxis(axis, angle);
		}
		mRotationDirty = false;
	}

	public Quaternion getOrientation(Quaternion qt)
	{
		setOrientation(); // Force
					// mOrientation
					// to be
					// recalculated
		qt.setAll(mOrientation);
		return qt;
	}

	public void setOrientation(Quaternion quat)
	{
		mOrientation.setAll(quat);
		mRotationDirty = false;
	}

	public void setRotation(double rotX, double rotY, double rotZ)
	{
		mRotation.x = rotX;
		mRotation.y = rotY;
		mRotation.z = rotZ;
		mRotationDirty = true;
	}

	public void setRotation(double[] rotationMatrix)
	{
		// TODO: This will be fixed by issue
		// #968
		mOrientation.fromRotationMatrix(rotationMatrix);
	}

	public void setRotX(double rotX)
	{
		mRotation.x = rotX;
		mRotationDirty = true;
	}

	public double getRotX()
	{
		return mRotation.x;
	}

	public void setRotY(double rotY)
	{
		mRotation.y = rotY;
		mRotationDirty = true;
	}

	public double getRotY()
	{
		return mRotation.y;
	}

	public void setRotZ(double rotZ)
	{
		mRotation.z = rotZ;
		mRotationDirty = true;
	}

	public double getRotZ()
	{
		return mRotation.z;
	}

	public Vector3 getRotation()
	{
		return mRotation;
	}

	public void setRotation(Vector3 rotation)
	{
		mRotation.setAll(rotation);
		mRotationDirty = true;
	}

	public void setScale(double scale)
	{
		mScale.x = scale;
		mScale.y = scale;
		mScale.z = scale;
	}

	public void setScale(double scaleX, double scaleY, double scaleZ)
	{
		mScale.x = scaleX;
		mScale.y = scaleY;
		mScale.z = scaleZ;
	}

	public void setScaleX(double scaleX)
	{
		mScale.x = scaleX;
	}

	public double getScaleX()
	{
		return mScale.x;
	}

	public void setScaleY(double scaleY)
	{
		mScale.y = scaleY;

	}

	public double getScaleY()
	{
		return mScale.y;
	}

	public void setScaleZ(double scaleZ)
	{
		mScale.z = scaleZ;
	}

	public double getScaleZ()
	{
		return mScale.z;
	}

	public Vector3 getScale()
	{
		return mScale;
	}

	public void setScale(Vector3 scale)
	{
		mScale.setAll(scale);
	}

	public Vector3 getLookAt()
	{
		return mLookAt;
	}

	public void setLookAt(double x, double y, double z)
	{
		if (mLookAt == null)
			mLookAt = new Vector3();
		mLookAt.x = x;
		mLookAt.y = y;
		mLookAt.z = z;
		mRotationDirty = true;
	}

	public void setLookAt(Vector3 lookAt)
	{
		if (lookAt == null)
		{
			mLookAt = null;
			return;
		}
		setLookAt(lookAt.x, lookAt.y, lookAt.z);
	}
}
