package ygame.domain;

import ygame.framework.domain.YWriteBundle;
import ygame.math.Matrix4;
import ygame.math.YATransformer;

public class YTransformer extends YATransformer
{
	private Matrix4 mMVPMatrix = new Matrix4();
	private Matrix4 mMMatrix = new Matrix4();
	private Matrix4 mMVMatrix = new Matrix4();
//	private Matrix4 mPMatrix;
	private Matrix4 mParentMatrix;
	private Matrix4 mRotationMatrix = new Matrix4();
	private Matrix4 mNormalMatrix = new Matrix4();
	private float[] mNormalFloats = new float[9];

	void calculate(Matrix4 matrix4VP, Matrix4 matrix4Projection,
			Matrix4 matrix4View, Matrix4 matrix4Parent,
			YWriteBundle bundle)
	{
		mParentMatrix = matrix4Parent;
		// -- move view matrix transformation
		// first
		calculateModelMatrix(matrix4Parent);
		// -- calculate model view matrix;
		mMVMatrix.setAll(matrix4View).multiply(mMMatrix);
		// Create MVP Matrix from
		// View-Projection Matrix
		mMVPMatrix.setAll(matrix4VP).multiply(mMMatrix);
//		mPMatrix = matrix4Projection;
		calculateNormalMatrix(mMMatrix);

		bundle.writeMVP_Matrix(mMVPMatrix.getFloatValues());
		bundle.writeModelMatrix(mMMatrix.getFloatValues());
		bundle.writeMV_Matrix(mMVMatrix.getFloatValues());
		bundle.writeNormalMatrix(mNormalFloats);
	}

	private void calculateModelMatrix(final Matrix4 parentMatrix)
	{
		setOrientation();
		if (mLookAt == null)
		{
			mOrientation.toRotationMatrix(mRotationMatrix);
		} else
		{
			mRotationMatrix.setAll(mLookAtMatrix);
		}
		mMMatrix.identity().translate(mPosition).scale(mScale)
				.multiply(mRotationMatrix);
		if (parentMatrix != null)
			mMMatrix.leftMultiply(mParentMatrix);
	}

	private void calculateNormalMatrix(Matrix4 modelMatrix)
	{
		mNormalMatrix.setAll(modelMatrix).setToNormalMatrix();
		float[] matrix = mNormalMatrix.getFloatValues();

		mNormalFloats[0] = matrix[0];
		mNormalFloats[1] = matrix[1];
		mNormalFloats[2] = matrix[2];
		mNormalFloats[3] = matrix[4];
		mNormalFloats[4] = matrix[5];
		mNormalFloats[5] = matrix[6];
		mNormalFloats[6] = matrix[8];
		mNormalFloats[7] = matrix[9];
		mNormalFloats[8] = matrix[10];
	}
}
