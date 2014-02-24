package ygame.extension.program;

import ygame.domain.YABaseShaderProgram;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YReadBundle;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;
import ygame.program.YAShaderProgram;
import ygame.program.YAttributeValue;
import ygame.skeleton.YSkeleton;
import ygame.transformable.YMover;
import android.content.res.Resources;
import android.opengl.GLES20;

/**
 * <b>内部用来继承的渲染程序</b>
 * 
 * <p>
 * <b>概述</b>： 不对外公布
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
abstract class YABlankProgram extends
		YAShaderProgram
{
	private static final int SKE = 0;
	private static final int PVM = 1;

	private int iDrawMode = GLES20.GL_TRIANGLES;
	private YAttributeValue aPosition;
	private YAttributeValue aColor;
	private YAttributeValue aNormal;
	private YAttributeValue aTexCoord;

	YABlankProgram(Resources resources, int iDrawMode)
	{
		this.iDrawMode = iDrawMode;
	}

	@Override
	protected void onInitialize(int iProgramHandle)
	{
		super.onInitialize(iProgramHandle);
		aPosition = getAttribute("aPosition");
		aColor = getAttribute("aColor");
		aNormal = getAttribute("aNormal");
		aTexCoord = getAttribute("aTexCoord");
	}

	@Override
	protected void applyParams(int iProgramHandle, YReadBundle bundle,
			YSystem system)
	{
		YSkeleton skeleton = (YSkeleton) bundle.readObject(SKE);
		bindAttribute(aPosition, skeleton.getPositionDataSource());
		if (null != aColor)
			bindAttribute(aColor, skeleton.getColorDataSource());
		if (null != aNormal)
			bindAttribute(aNormal, skeleton.getNormalDataSource());
		if (null != aTexCoord)
			bindAttribute(aTexCoord,
					skeleton.getTexCoordDataSource());

		setUniformMatrix("uPVMMatrix", bundle.readFloatArray(PVM));

		drawWithIBO(skeleton.getIndexHandle(), skeleton.getVertexNum(),
				iDrawMode);
	}

	/**
	 * <b>参数适配器</b>
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
	 * <b>注</b>：您每次使用该适配器时，应该全部填写其前缀名为<b>param</b
	 * >的方法
	 * 
	 * <p>
	 * <b>例</b>：TODO
	 * @author yunzhong
	 * 
	 */
	static abstract class YABlankAdapter<BA extends YABlankAdapter<?>> extends
			YABaseShaderProgram.YABaseParametersAdapter
	{
		private YMover mover;
		private YMatrix matrixPV;
		private YSkeleton skeleton;

		private YMatrix matrixPVM = new YMatrix();

		/**
		 * @param mover
		 *                移动子
		 * @return 参数适配器
		 */
		@SuppressWarnings("unchecked")
		public BA paramMover(YMover mover)
		{
			this.mover = mover;
			return (BA) this;
		}

		/**
		 * @param matrixPV
		 *                投影-视图矩阵
		 * @return 参数适配器
		 */
		@SuppressWarnings("unchecked")
		public BA paramMatrixPV(YMatrix matrixPV)
		{
			this.matrixPV = matrixPV;
			return (BA) this;
		}

		/**
		 * @param skeleton
		 *                渲染的骨架
		 * @return 参数适配器
		 */
		@SuppressWarnings("unchecked")
		public BA paramSkeleton(YSkeleton skeleton)
		{
			this.skeleton = skeleton;
			return (BA) this;
		}

		@Override
		protected void bundleMapping(YWriteBundle bundle)
		{
			bundle.writeObject(SKE, skeleton);

			YMatrix.multiplyMM(matrixPVM, matrixPV,
					mover.getMatrix());
			bundle.writeFloatArray(PVM, matrixPVM.toFloatValues());
		}
	}
}
