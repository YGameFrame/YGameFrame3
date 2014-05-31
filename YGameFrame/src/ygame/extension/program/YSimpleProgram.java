package ygame.extension.program;

import ygame.domain.YABaseShaderProgram;
import ygame.domain.YDomainView;
import ygame.framework.R;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YReadBundle;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;
import ygame.program.YAShaderProgram;
import ygame.skeleton.YSkeleton;
import ygame.transformable.YMover;
import ygame.utils.YTextFileUtils;
import android.content.res.Resources;

/**
 * <b>简单渲染程序</b>
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
public final class YSimpleProgram extends YAShaderProgram
{
	// private int iPositionHandle = -1;
	// private int iColorHandle = -1;
	// private int iPVMMatrixHandle = -1;

	// private YAttributeValue aPosition;
	// private YAttributeValue aColor;
	private static YSimpleProgram instance;

	public static YSimpleProgram getInstance(Resources resources)
	{
		if (null == instance)
			synchronized (YSimpleProgram.class)
			{
				if (null == instance)
					instance = new YSimpleProgram(resources);
			}
		return instance;
//		return new YSimpleProgram(resources);
	}

	private YSimpleProgram(Resources resources)
	{
		fillCodeAndParam(YTextFileUtils.getStringFromResRaw(
				R.raw.simple_vsh, resources),
				YTextFileUtils.getStringFromResRaw(
						R.raw.simple_fsh, resources),
				YAdapter.class);
	}

	// @Override
	// protected void onInitialize(int
	// iProgramHandle)
	// {
	// iPositionHandle =
	// GLES20.glGetAttribLocation(iProgramHandle,
	// "aPosition");
	// iColorHandle =
	// GLES20.glGetAttribLocation(iProgramHandle,
	// "aColor");
	// // super.onInitialize(iProgramHandle);
	//
	//
	// // aPosition = getAttribute("aPosition");
	// // aColor = getAttribute("aColor");
	// iPVMMatrixHandle =
	// GLES20.glGetUniformLocation(iProgramHandle,
	// "uPVMMatrix");
	// }

	@Override
	protected void applyParams(int iProgramHandle, YReadBundle bundle,
			YSystem system, YDomainView domainView)
	{
		YSkeleton skeleton = (YSkeleton) bundle
				.readObject(YAdapter.SKELETON);
		setAttribute("aPosition", skeleton.getPositionDataSource());
		setAttribute("aColor", skeleton.getColorDataSource());
		setUniformMatrix(
				"uPVMMatrix",
				bundle.readFloatArray(YAdapter.MATRIX_PVM));

		// setAttribute(aColor,
		// skeleton.getColorData());
		// setAttribute(aPosition,
		// skeleton.getPositionData());
		// bindVBODataSourcToValue(skeleton.getColorHandle(),
		// iColorHandle, 4);
		// bindVBODataSourcToValue(skeleton.getPositionHandle(),
		// iPositionHandle, 3);

		// GLES20.glUniformMatrix4fv(
		// iPVMMatrixHandle,
		// 1,
		// false,
		// bundle.readFloatArray(YSimpleParamAdapter.MATRIX_PVM),
		// 0);

		drawWithIBO(skeleton.getIndexHandle(), skeleton.getVertexNum(),
				domainView.iDrawMode);
	}

	/**
	 * <b>简单渲染程序的参数适配器</b>
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
	 * <b>注</b>：您每次使用该适配器时，应该全部填写其前缀名为<b>param</b >的方法
	 * 
	 * <p>
	 * <b>例</b>：
	 * <code><dd>YSimpleParamAdapter dataAdapter = (YSimpleParamAdapter) domainContext
				.getParametersAdapter();</br>
		<dd>dataAdapter.paramMatrixPV(matrix4PV).paramMover(mover)
				.paramSkeleton(skeleton);</br></code>
	 * 
	 * @author yunzhong
	 * 
	 */
	public static class YAdapter extends
			YABaseShaderProgram.YABaseParametersAdapter
	{
		private static final int SKELETON = 0;
		private static final int MATRIX_PVM = 1;

		private YMover mover;
		private YMatrix matrixPV;
		private YSkeleton skeleton;

		private YMatrix matrixPVM = new YMatrix();

		/**
		 * @param mover
		 *                移动子
		 * @return 参数适配器
		 */
		public YAdapter paramMover(YMover mover)
		{
			this.mover = mover;
			return this;
		}

		/**
		 * @param matrixPV
		 *                投影-视图矩阵
		 * @return 参数适配器
		 */
		public YAdapter paramMatrixPV(YMatrix matrixPV)
		{
			this.matrixPV = matrixPV;
			return this;
		}

		/**
		 * @param skeleton
		 *                渲染的骨架
		 * @return 参数适配器
		 */
		public YAdapter paramSkeleton(YSkeleton skeleton)
		{
			this.skeleton = skeleton;
			return this;
		}

		@Override
		protected void bundleMapping(YWriteBundle bundle)
		{
			bundle.writeObject(SKELETON, skeleton);

			YMatrix.multiplyMM(matrixPVM, matrixPV,
					mover.getMatrix());
			bundle.writeFloatArray(MATRIX_PVM,
					matrixPVM.toFloatValues());
		}
	}
}
