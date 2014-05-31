package ygame.extension.program;

import ygame.domain.YDomainView;
import ygame.framework.R;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YReadBundle;
import ygame.framework.domain.YWriteBundle;
import ygame.texture.YTileSheet;
import ygame.texture.YTexture;
import ygame.utils.YTextFileUtils;
import android.content.res.Resources;

/**
 * <b>瓷砖渲染程序</b>
 * 
 * <p>
 * <b>概述</b>： 该程序可以指定<b>瓷砖图</b>{@link YTileSheet}中某一瓷砖块渲染至骨架
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
public final class YTileProgram extends YABlankProgram
{
	private static final int FTP = 2;// FrameTexParam
	private static final int TEX = 3;

	private static YTileProgram instance;

	public static YTileProgram getInstance(Resources resources)
	{
		if (null == instance)
			synchronized (YTileMapProgram.class)
			{
				if (null == instance)
					instance = new YTileProgram(resources);
			}
		return instance;
//		return new YTileProgram(resources);
	}

	private YTileProgram(Resources resources)
	{
		super(resources);
		fillCodeAndParam(YTextFileUtils.getStringFromResRaw(
				R.raw.frame_vsh, resources),
				YTextFileUtils.getStringFromResRaw(
						R.raw.texture_fsh, resources),
				YAdapter.class);
	}

	@Override
	protected void applyParams(int iProgramHandle, YReadBundle bundle,
			YSystem system, YDomainView domainView)
	{
		float[] ftp = bundle.readFloatArray(FTP);
		setUniformf("uFrameTexParam", ftp[0], ftp[1], ftp[2], ftp[3]);

		setUniformTexture("sTexture",
				(YTexture) bundle.readObject(TEX), 0);
		super.applyParams(iProgramHandle, bundle, system, domainView);
	}

	public static class YAdapter extends YABlankAdapter<YAdapter>
	{
		// 元素为：[行索引(0开始计)、列索引(0开始计)、小格宽度、小格高度]
		private final float[] f_arrFrameTexParam = new float[4];

		private YTileSheet frameSheet;

		/**
		 * 要渲染的瓷砖所处的行列数
		 * 
		 * @param iRowIndex
		 *                行数（以0开始计）
		 * @param iColumnIndex
		 *                列数（以0开始计）
		 * @return 适配器
		 */
		public YAdapter paramFramePosition(int iRowIndex,
				int iColumnIndex)
		{
			f_arrFrameTexParam[0] = iRowIndex;
			f_arrFrameTexParam[1] = iColumnIndex;
			return this;
		}

		/**
		 * 要渲染的瓷砖图
		 * 
		 * @param tileSheet
		 *                瓷砖图
		 * @return 适配器
		 */
		public YAdapter paramFrameSheet(YTileSheet tileSheet)
		{
			this.frameSheet = tileSheet;
			f_arrFrameTexParam[2] = tileSheet.GRID_WIDTH;
			f_arrFrameTexParam[3] = tileSheet.GRID_HEIGHT;
			return this;
		}

		@Override
		protected void bundleMapping(YWriteBundle bundle)
		{
			bundle.writeObject(TEX, frameSheet);
			bundle.writeFloatArray(FTP, f_arrFrameTexParam);
			super.bundleMapping(bundle);
		}
	}
}
