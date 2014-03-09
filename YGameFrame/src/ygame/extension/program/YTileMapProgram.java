package ygame.extension.program;

import ygame.domain.YDomainView;
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
public final class YTileMapProgram extends YABlankProgram
{
	private static final int TEX_DATA = 2;
	private static final int TILE_SHEET = 3;

	//private static YTileMapProgram instance;

	public static YTileMapProgram getInstance(Resources resources)
	{
//		if (null == instance)
//			synchronized (YTileMapProgram.class)
//			{
//				if (null == instance)
//					instance = new YTileMapProgram(resources);
//			}
//		return instance;
		return new YTileMapProgram(resources);
	}

	private YTileMapProgram(Resources resources)
	{
		super(resources);
		fillCodeAndParam(YTextFileUtils.getStringFromResRaw(
				R.raw.texture_vsh, resources),
				YTextFileUtils.getStringFromResRaw(
				// R.raw.test_tilemap_fsh,
						R.raw.tilemap_fsh, resources),
				YTileMapAdapter.class);
	}

	@Override
	protected void applyParams(int iProgramHandle, YReadBundle bundle,
			YSystem system, YDomainView domainView)
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
		super.applyParams(iProgramHandle, bundle, system, domainView);
	}

	// public static Bitmap transformTiledJsonToBitmap(String strJson)
	// {
	// JSONTokener tokener = new JSONTokener(strJson);
	// try
	// {
	// JSONObject jsonObj = (JSONObject) tokener.nextValue();
	// JSONArray layers = jsonObj.getJSONArray("layers");
	// JSONObject layer0 = layers.getJSONObject(0);
	// int iHeight = layer0.getInt("height");
	// int iWidth = layer0.getInt("width");
	// int iDataLen = iHeight * iWidth;
	// int[] dataColors = new int[iDataLen];
	//
	// JSONArray dataObj = layers.getJSONObject(1)
	// .getJSONArray("data");
	// JSONArray dataBkg = layer0.getJSONArray("data");
	// for (int i = 0; i < iDataLen; i++)
	// {
	// if (0 == dataBkg.getInt(i)
	// && 0 == dataObj.getInt(i))
	// dataColors[i] = 0xff000000;
	// else if (0 == dataBkg.getInt(i)
	// && 0 != dataObj.getInt(i))
	// dataColors[i] = (dataObj.getInt(i) - 1) * 256 + 0xff260000;
	// // dataColors[i] = 0xff000000;
	// else if (0 != dataBkg.getInt(i)
	// && 0 == dataObj.getInt(i))
	// dataColors[i] = dataBkg.getInt(i) - 1 + 0xff400000;
	// else
	// dataColors[i] = dataBkg.getInt(i)
	// - 1
	// + (dataObj.getInt(i) - 1)
	// * 256 + 0xff5A0000;
	// // dataColors[i] = 0xff000000;
	// }
	//
	// return Bitmap.createBitmap(dataColors, iWidth, iHeight,
	// Bitmap.Config.ARGB_8888);
	//
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// return null;
	// }
	// }

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
