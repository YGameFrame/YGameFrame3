package ygame.extension.program;

import ygame.framework.R;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YReadBundle;
import ygame.framework.domain.YWriteBundle;
import ygame.texture.YTexture;
import ygame.texture.YTileSheet;
import ygame.utils.YTextFileUtils;
import android.content.res.Resources;

/**
 * <b>瓷砖地图程序</b>
 * 
 * <p>
 * <b>概述</b>： 该程序通过索引图以及地图矩阵延迟绘制出最终地图
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
public class YTileMapProgram extends YABlankProgram
{
	private static final int TEX_DATA = 2;
	private static final int TILE_SHEET = 3;

	public YTileMapProgram(Resources resources, int iDrawMode)
	{
		super(resources, iDrawMode);
		fillCodeAndParam(YTextFileUtils.getStringFromResRaw(
				R.raw.texture_vsh, resources),
				YTextFileUtils.getStringFromResRaw(
						R.raw.tilemap_fsh, resources),
				YTileMapAdapter.class);
	}

	@Override
	protected void applyParams(int iProgramHandle, YReadBundle bundle,
			YSystem system)
	{
		YTileSheet textureGraphic = (YTileSheet) bundle
				.readObject(TILE_SHEET);
		setUniformTexture("uTileGraphic", textureGraphic, 0);
		setUniformf("uTileGraphicInfo", textureGraphic.ROW_NUM,
				textureGraphic.COLUMN_NUM,
				textureGraphic.GRID_HEIGHT,
				textureGraphic.GRID_WIDTH);

		YTexture textureData = (YTexture) bundle.readObject(TEX_DATA);
		setUniformTexture("uTileData", textureData, 1);
		setUniformf("uTileDataInfo", 1.0f / textureData.HEIGHT,
				1.0f / textureData.WIDTH);
		super.applyParams(iProgramHandle, bundle, system);
	}

	public static class YTileMapAdapter extends
			YABlankAdapter<YTileMapAdapter>
	{
		private YTexture textureData;
		private YTileSheet tileSheetGraphics;

		/**
		 * @param textureData
		 *                存储地图矩阵数据的纹理
		 * @return 适配器
		 */
		public YTileMapAdapter paramTileMapData(YTexture textureData)
		{
			this.textureData = textureData;
			return this;
		}

		/**
		 * @param tileSheetGraphics
		 *                索引图
		 * @return 适配器
		 */
		public YTileMapAdapter paramTileMapGraphics(
				YTileSheet tileSheetGraphics)
		{
			this.tileSheetGraphics = tileSheetGraphics;
			return this;
		}

		@Override
		protected void bundleMapping(YWriteBundle bundle)
		{
			bundle.writeObject(TILE_SHEET, tileSheetGraphics);
			bundle.writeObject(TEX_DATA, textureData);
			super.bundleMapping(bundle);
		}
	}

}
