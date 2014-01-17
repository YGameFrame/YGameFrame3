package ygame.math;

import android.opengl.Matrix;

/**
*<b>4阶矩阵</b>
*
*<p>
*<b>概述</b>：
*TODO
*
*<p>
*<b>建议</b>：
*TODO
*
*<p>
*<b>详细</b>：
*TODO
*
*<p>
*<b>注</b>：
*TODO
*
*<p>
*<b>例</b>：
*TODO
*
*@author yunzhong
*
*/
public final class YMatrix
{

	// Matrix indices as column major notation
	// (Row x Column)
	public static final int M00 = 0; // 0;
	public static final int M01 = 4; // 1;
	public static final int M02 = 8; // 2;
	public static final int M03 = 12; // 3;
	public static final int M10 = 1; // 4;
	public static final int M11 = 5; // 5;
	public static final int M12 = 9; // 6;
	public static final int M13 = 13; // 7;
	public static final int M20 = 2; // 8;
	public static final int M21 = 6; // 9;
	public static final int M22 = 10; // 10;
	public static final int M23 = 14; // 11;
	public static final int M30 = 3; // 12;
	public static final int M31 = 7; // 13;
	public static final int M32 = 11; // 14;
	public static final int M33 = 15; // 15;

	private float[] f_arrData;

	public YMatrix()
	{
		f_arrData = new float[16];
	}

	public void setData(float[] f_arrData)
	{
		this.f_arrData = f_arrData;
	}

	public float[] toFloatValues()
	{
		return f_arrData;
	}

	public void setAll(YMatrix matrix)
	{
		System.arraycopy(matrix.f_arrData, 0, this.f_arrData, 0, 16);
	}

	public static void multiplyMM(YMatrix matrixRes, YMatrix matrixLeft,
			YMatrix matrixRight)
	{
		Matrix.multiplyMM(matrixRes.toFloatValues(), 0,
				matrixLeft.toFloatValues(), 0,
				matrixRight.toFloatValues(), 0);
	}

	/**
	 * 转置矩阵4*4
	 */
	public static void transposeM(YMatrix matrixRes, YMatrix matrix)
	{
		Matrix.transposeM(matrixRes.toFloatValues(), 0,
				matrix.toFloatValues(), 0);
	}

	/**
	 * 逆矩阵4*4
	 */
	public static boolean invertM(YMatrix matrixRes, YMatrix matrix)
	{
		return Matrix.invertM(matrixRes.toFloatValues(), 0,
				matrix.toFloatValues(), 0);
	}

	/**
	 * Computes an orthographic projection
	 * matrix.
	 */
	public void ortho(float left, float right, float bottom, float top,
			float near, float far)
	{
		Matrix.orthoM(toFloatValues(), 0, left, right, bottom, top,
				near, far);
	}

	/**
	 * Define a projection matrix in terms of six
	 * clip planes
	 */
	public void frustum(float left, float right, float bottom, float top,
			float near, float far)
	{
		Matrix.frustumM(toFloatValues(), 0, left, right, bottom, top,
				near, far);
	}

	/**
	 * Define a projection matrix in terms of a
	 * field of view angle, an aspect ratio, and
	 * z clip planes
	 * 
	 * @param f_arrData
	 *                the float array that holds
	 *                the perspective matrix
	 * @param offset
	 *                the offset into float array
	 *                f_arrData where the
	 *                perspective matrix data is
	 *                written
	 * @param fovy
	 *                field of view in y
	 *                direction, in degrees
	 * @param aspect
	 *                width to height aspect
	 *                ratio of the viewport
	 * @param zNear
	 * @param zFar
	 */
	public void perspective(float fovy, float aspect, float zNear,
			float zFar)
	{
		Matrix.perspectiveM(toFloatValues(), 0, fovy, aspect, zNear,
				zFar);
	}

	/**
	 * Computes the length of a vector
	 * 
	 * @param x
	 *                x coordinate of a vector
	 * @param y
	 *                y coordinate of a vector
	 * @param z
	 *                z coordinate of a vector
	 * @return the length of a vector
	 */
	public static float length(float x, float y, float z)
	{
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Sets matrix f_arrData to the identity
	 * matrix.
	 * 
	 * @param sm
	 *                returns the result
	 * @param smOffset
	 *                index into sm where the
	 *                result matrix starts
	 * @return
	 */
	public YMatrix identity()
	{
		Matrix.setIdentityM(toFloatValues(), 0);
		return this;
	}

	/**
	 * Scales matrix f_arrData by x, y, and z,
	 * putting the result in sm
	 * 
	 * @param sm
	 *                returns the result
	 * @param smOffset
	 *                index into sm where the
	 *                result matrix starts
	 * @param f_arrData
	 *                source matrix
	 * @param mOffset
	 *                index into f_arrData where
	 *                the source matrix starts
	 * @param x
	 *                scale factor x
	 * @param y
	 *                scale factor y
	 * @param z
	 *                scale factor z
	 */
	public static void scaleM(YMatrix matrixRes, YMatrix matrix, float x,
			float y, float z)
	{
		Matrix.scaleM(matrixRes.toFloatValues(), 0,
				matrix.toFloatValues(), 0, x, y, z);
	}

	/**
	 * Scales matrix f_arrData in place by sx,
	 * sy, and sz
	 * 
	 * @param f_arrData
	 *                matrix to scale
	 * @param mOffset
	 *                index into f_arrData where
	 *                the matrix starts
	 * @param x
	 *                scale factor x
	 * @param y
	 *                scale factor y
	 * @param z
	 *                scale factor z
	 */
	public void scale(float x, float y, float z)
	{
		Matrix.scaleM(toFloatValues(), 0, x, y, z);
	}

	/**
	 * Translates matrix f_arrData by x, y, and
	 * z, putting the result in tm
	 * 
	 * @param tm
	 *                returns the result
	 * @param tmOffset
	 *                index into sm where the
	 *                result matrix starts
	 * @param f_arrData
	 *                source matrix
	 * @param mOffset
	 *                index into f_arrData where
	 *                the source matrix starts
	 * @param x
	 *                translation factor x
	 * @param y
	 *                translation factor y
	 * @param z
	 *                translation factor z
	 */
	public static void translateM(YMatrix matrixRes, YMatrix matrix,
			float x, float y, float z)
	{
		Matrix.translateM(matrixRes.toFloatValues(), 0,
				matrix.toFloatValues(), 0, x, y, z);
	}

	/**
	 * Translates matrix f_arrData by x, y, and z
	 * in place.
	 * 
	 * @param f_arrData
	 *                matrix
	 * @param mOffset
	 *                index into f_arrData where
	 *                the matrix starts
	 * @param x
	 *                translation factor x
	 * @param y
	 *                translation factor y
	 * @param z
	 *                translation factor z
	 * @return
	 */
	public YMatrix translate(float x, float y, float z)
	{
		Matrix.translateM(toFloatValues(), 0, x, y, z);
		return this;
	}

	/**
	 * Rotates matrix f_arrData by angle a (in
	 * degrees) around the axis (x, y, z)
	 * 
	 * @param rm
	 *                returns the result
	 * @param rmOffset
	 *                index into rm where the
	 *                result matrix starts
	 * @param f_arrData
	 *                source matrix
	 * @param mOffset
	 *                index into f_arrData where
	 *                the source matrix starts
	 * @param a
	 *                angle to rotate in degrees
	 * @param x
	 *                scale factor x
	 * @param y
	 *                scale factor y
	 * @param z
	 *                scale factor z
	 */
	public static void rotateM(YMatrix matrixRes, YMatrix matrix, float a,
			float x, float y, float z)
	{
		Matrix.rotateM(matrixRes.toFloatValues(), 0,
				matrix.toFloatValues(), 0, a, x, y, z);
	}

	/**
	 * Rotates matrix f_arrData in place by angle
	 * a (in degrees) around the axis (x, y, z)
	 * 
	 * @param f_arrData
	 *                source matrix
	 * @param mOffset
	 *                index into f_arrData where
	 *                the matrix starts
	 * @param a
	 *                angle to rotate in degrees
	 * @param x
	 *                scale factor x
	 * @param y
	 *                scale factor y
	 * @param z
	 *                scale factor z
	 */
	public void rotate(float a, float x, float y, float z)
	{
		Matrix.rotateM(toFloatValues(), 0, a, x, y, z);
	}

	/**
	 * Rotates matrix f_arrData by angle a (in
	 * degrees) around the axis (x, y, z)
	 * 
	 * @param rm
	 *                returns the result
	 * @param rmOffset
	 *                index into rm where the
	 *                result matrix starts
	 * @param a
	 *                angle to rotate in degrees
	 * @param x
	 *                scale factor x
	 * @param y
	 *                scale factor y
	 * @param z
	 *                scale factor z
	 */
	public static void setRotateM(YMatrix matrixRes, float a, float x,
			float y, float z)
	{
		Matrix.setRotateM(matrixRes.toFloatValues(), 0, a, x, y, z);
	}

	/**
	 * Converts Euler angles to a rotation matrix
	 * 
	 * @param rm
	 *                returns the result
	 * @param rmOffset
	 *                index into rm where the
	 *                result matrix starts
	 * @param x
	 *                angle of rotation, in
	 *                degrees
	 * @param y
	 *                angle of rotation, in
	 *                degrees
	 * @param z
	 *                angle of rotation, in
	 *                degrees
	 */
	public void setRotateEulerM(float x, float y, float z)
	{
		Matrix.setRotateEulerM(toFloatValues(), 0, x, y, z);
	}

	/**
	 * Define a viewing transformation in terms
	 * of an eye point, a center of view, and an
	 * up vector.
	 * 
	 * @param rm
	 *                returns the result
	 * @param rmOffset
	 *                index into rm where the
	 *                result matrix starts
	 * @param eyeX
	 *                eye point X
	 * @param eyeY
	 *                eye point Y
	 * @param eyeZ
	 *                eye point Z
	 * @param targetX
	 *                center of view X
	 * @param targetY
	 *                center of view Y
	 * @param targetZ
	 *                center of view Z
	 * @param upX
	 *                up vector X
	 * @param upY
	 *                up vector Y
	 * @param upZ
	 *                up vector Z
	 * @return
	 */
	public YMatrix setLookAtM(float eyeX, float eyeY, float eyeZ,
			float targetX, float targetY, float targetZ, float upX,
			float upY, float upZ)
	{
		Matrix.setLookAtM(toFloatValues(), 0, eyeX, eyeY, eyeZ,
				targetX, targetY, targetZ, upX, upY, upZ);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + f_arrData[M00] + "|" + f_arrData[M01] + "|"
				+ f_arrData[M02] + "|" + f_arrData[M03]
				+ "]\n[" + f_arrData[M10] + "|"
				+ f_arrData[M11] + "|" + f_arrData[M12] + "|"
				+ f_arrData[M13] + "]\n[" + f_arrData[M20]
				+ "|" + f_arrData[M21] + "|" + f_arrData[M22]
				+ "|" + f_arrData[M23] + "]\n["
				+ f_arrData[M30] + "|" + f_arrData[M31] + "|"
				+ f_arrData[M32] + "|" + f_arrData[M33] + "]\n";
	}
}