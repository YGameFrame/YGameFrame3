package ygame.extension;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ygame.exception.YException;

import android.graphics.Bitmap;

/**
 * <b>解析Tiled工具生成的json文件解析器</b>
 * 
 * <p>
 * <b>概述</b>：利用Tiled工具生成瓷砖地图后，可以导出其json文件，然后 利用该类进行解析。
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>： 地图的图层只应该有两层，并且其主要背景层命名为“base_bkg”，修饰层命名为“decoration_bkg”。
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public class YTiledJsonParser
{
	private static final String DECORATION_BKG = "decoration_bkg";
	private static final String BASE_BKG = "base_bkg";
	final protected JSONObject jsonTree;
	final private int iRowNum;
	final private int iColumnNum;

	private JSONObject jsonIndexPic;

	public final float fRealWorldToTileWorldRadio;

	/**
	 * @param strTiledJson
	 * @param fRealWorldToTileWorldRadio
	 *                真实世界到瓷砖世界的比例（即一个砖块大概是多少米）
	 * @throws JSONException
	 *                 json异常
	 */
	public YTiledJsonParser(String strTiledJson,
			float fRealWorldToTileWorldRadio) throws JSONException
	{
		this.fRealWorldToTileWorldRadio = fRealWorldToTileWorldRadio;
		jsonTree = (JSONObject) new JSONTokener(strTiledJson)
				.nextValue();
		iRowNum = jsonTree.getInt("height");
		iColumnNum = jsonTree.getInt("width");
		jsonIndexPic = jsonTree.getJSONArray("tilesets").getJSONObject(
				0);

		int num = getIndexPicTileTypeNum();
		if (num > 255)
			throw new YException("索引图砖块种类数目超出限制！", getClass()
					.getName(),
					"索引图砖块种类不可超过256种，该索引图砖块数目为：" + num);
	}

	private int getIndexPicTileTypeNum() throws JSONException
	{
		int row = jsonIndexPic.getInt("imageheight")
				/ jsonIndexPic.getInt("tileheight");
		int column = jsonIndexPic.getInt("imagewidth")
				/ jsonIndexPic.getInt("tilewidth");
		return row * column;
	}

	/**
	 * 解析最终地图的列数
	 * 
	 * @return 最终地图列数
	 */
	public final int parseFinalMapColumnNum()
	{
		return iColumnNum;
	}

	/**
	 * 解析最终地图的行数
	 * 
	 * @return 最终地图行数
	 */
	public final int parseFinalMapRowNum()
	{
		return iRowNum;
	}

	/**
	 * 解析索引图的行数
	 * 
	 * @return 索引图行数
	 * @throws JSONException
	 *                 json异常
	 */
	public final int parseIndexPicRowNum() throws JSONException
	{
		return jsonIndexPic.getInt("imageheight")
				/ jsonIndexPic.getInt("tileheight");
	}

	/**
	 * 解析索引图的列数
	 * 
	 * @return 索引图列数
	 * @throws JSONException
	 *                 json异常
	 */
	public final int parseIndexPicColumnNum() throws JSONException
	{
		return jsonIndexPic.getInt("imagewidth")
				/ jsonIndexPic.getInt("tilewidth");
	}

	/**
	 * 将基础背景层（base_bkg）及修饰背景层（decoration_bkg）解析为整型数组数据
	 * 
	 * @return 蕴含数据的整型数组
	 * @throws JSONException
	 *                 json解析异常
	 */
	public final int[] parseGraphicLayersAsArray() throws JSONException
	{
		int iDataSize = iRowNum * iColumnNum;
		int[] dataColors = new int[iDataSize];

		JSONArray layers = jsonTree.getJSONArray("layers");
		JSONArray dataDecorationBkg = null;
		JSONArray dataBaseBkg = null;
		int iLayersSize = layers.length();
		for (int i = 0; i < iLayersSize; i++)
		{
			JSONObject layer = layers.getJSONObject(i);
			String name = layer.optString("name");
			if (name.equals(BASE_BKG))
				dataBaseBkg = layer.getJSONArray("data");
			else if (name.equals(DECORATION_BKG))
				dataDecorationBkg = layer.getJSONArray("data");
		}

		if (null == dataBaseBkg)
			throw new YException("地图的基础背景层" + BASE_BKG + "为空",
					getClass().getName(),
					"请创建地图基础背景层，并命名为：" + BASE_BKG);

		if (null == dataDecorationBkg)
		{
			for (int i = 0; i < iDataSize; i++)
			{
				if (0 == dataBaseBkg.getInt(i))
					dataColors[i] = 0xff000000;
				else if (0 != dataBaseBkg.getInt(i))
					dataColors[i] = dataBaseBkg.getInt(i) - 1 + 0xff400000;
			}
		} else
		{
			for (int i = 0; i < iDataSize; i++)
			{
				if (0 == dataBaseBkg.getInt(i)
						&& 0 == dataDecorationBkg
								.getInt(i))
					dataColors[i] = 0xff000000;
				else if (0 == dataBaseBkg.getInt(i)
						&& 0 != dataDecorationBkg
								.getInt(i))
					dataColors[i] = (dataDecorationBkg
							.getInt(i) - 1) * 256 + 0xff260000;
				else if (0 != dataBaseBkg.getInt(i)
						&& 0 == dataDecorationBkg
								.getInt(i))
					dataColors[i] = dataBaseBkg.getInt(i) - 1 + 0xff400000;
				else
					dataColors[i] = dataBaseBkg.getInt(i)
							- 1
							+ (dataDecorationBkg
									.getInt(i) - 1)
							* 256 + 0xff5A0000;
			}
		}

		return dataColors;
	}

	/**
	 * 将基础背景层（base_bkg）及修饰背景层（decoration_bkg）直接解析为数据纹理</br>
	 * <b>注</b>：如果您需要对其数据进行额外处理的话，建议调用{@link #parseGraphicLayersAsArray()}。
	 * 
	 * @return 蕴含数据的纹理
	 * @throws JSONException
	 *                 json解析异常
	 */
	public final Bitmap parseGraphicLayersAsBitmap() throws JSONException
	{
		return Bitmap.createBitmap(parseGraphicLayersAsArray(),
				iColumnNum, iRowNum, Bitmap.Config.ARGB_8888);
	}
}
