package ygame.extension.domain.tilemap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;

import ygame.domain.YABaseShaderProgram;
import ygame.domain.YDomain;
import ygame.domain.YDomainView;
import ygame.extension.YTiledJsonParser;
import ygame.extension.primitives.YRectangle;
import ygame.extension.program.YTileMapProgram;
import ygame.framework.core.YABaseDomain;
import ygame.framework.core.YClusterDomain;
import ygame.framework.core.YSystem;
import ygame.math.vector.Vector2;
import ygame.skeleton.YSkeleton;
import ygame.texture.YTexture;
import ygame.texture.YTileSheet;
import android.content.res.Resources;
import android.graphics.Bitmap;

/**
 * <b>瓷砖地图</b>
 * 
 * <p>
 * <b>概述</b>： 您可以利用<b>瓷砖解析器</b>及<b>索引图</b>构建一个超大、清晰的瓷砖地图实体。
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： 你可以通过指定划分列数和划分行数来提高画面质量。
 * 
 * <p>
 * <b>注</b>： TODO
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 * 
 */
// 采用了分块和程序纹理技术，利用少量顶点生成超大清晰的瓷砖地图
public class YTileMapDomain extends YClusterDomain
{
	private static final int SKE_NORMAL = 0;
	private static final int SKE_RIGHT = 1;
	private static final int SKE_BOTTOM = 2;
	private static final int SKE_RIGHT_BOTTOM = 3;

	private YSkeleton skeletons[] = new YSkeleton[4];

	private final float fRealWorldToTileWorldRadio;
	private Collection<YABaseDomain> componentDomains;

	public YTileMapDomain(String KEY, Resources resources,
			int iIndexPicResId, YTiledJsonParser parser)
			throws JSONException
	{
		this(KEY, resources, iIndexPicResId, parser, 1, 1);
	}

	/**
	 * 建议32行、32列为一个切分整体即可获得较好效果
	 * 
	 * @param KEY
	 *                实体标识
	 * @param resources
	 *                资源
	 * @param iIndexPicResId
	 *                索引图资源标识
	 * @param parser
	 *                解析器
	 * @param iDividedColumn
	 *                划分列数，将大地图大致切分为多少列，剩余部分会被保留，但要注意剩余部分不应大于拆分部分
	 * @param iDividedRow
	 *                划分行数，将大地图大致切分为多少行，剩余部分会被保留，但要注意剩余部分不应大于拆分部分
	 * @throws JSONException
	 *                 json异常
	 */
	public YTileMapDomain(String KEY, Resources resources,
			int iIndexPicResId, YTiledJsonParser parser,
			int iDividedColumn, int iDividedRow)
			throws JSONException
	{
		super(KEY);
		this.fRealWorldToTileWorldRadio = parser.fRealWorldToTileWorldRadio;
		componentDomains = createComponentDomains(resources,
				iIndexPicResId, parser, iDividedColumn,
				iDividedRow);
	}

	private Collection<YABaseDomain> createComponentDomains(
			Resources resources, int iIndexPicResId,
			YTiledJsonParser parser, int iDividedColumn,
			int iDividedRow) throws JSONException
	{
		YTileSheet tileSheet = new YTileSheet(iIndexPicResId,
				resources, parser.parseIndexPicRowNum(),
				parser.parseIndexPicColumnNum());
		int[] dataArray = parser.parseGraphicLayersAsArray();
		YMapComponentMetaData[] componentMetaDatas = divide(dataArray,
				parser.parseFinalMapColumnNum(),
				parser.parseFinalMapRowNum(), iDividedColumn,
				iDividedRow);
		YABaseShaderProgram program = YTileMapProgram
				.getInstance(resources);
		List<YABaseDomain> domainComponents = new ArrayList<YABaseDomain>();
		for (YMapComponentMetaData metaData : componentMetaDatas)
		{
			YMapComponentData data = createComponentData(metaData,
					parser.parseFinalMapColumnNum(),
					parser.parseFinalMapRowNum(), tileSheet);
			YDomain componentDomain = new YDomain(
					metaData.iIndex + "",
					new YLargeMapComponentDomainLogic(data),
					new YDomainView(program));
			domainComponents.add(componentDomain);
			// if(domainComponents.size() == 4)
			// return;
		}
		return domainComponents;
	}

	@Override
	protected void onAttach(YSystem system)
	{
		super.onAttach(system);
		addComponentDomains(componentDomains);
		componentDomains = null;
	}

	private YMapComponentData createComponentData(
			YMapComponentMetaData metaData, float fFinalMapColumn,
			float fFinalMapRow, YTileSheet tileSheet)
	{
		YMapComponentData data = new YMapComponentData();
		data.skeleton = getSkeleton(metaData);
		data.textureData = new YTexture(Bitmap.createBitmap(
				metaData.data, metaData.iWidth,
				metaData.iHeight, Bitmap.Config.ARGB_8888));
		data.tileSheet = tileSheet;
		Vector2 vector2 = new Vector2(metaData.iColIndex
				* metaData.iNormalWidth, -metaData.iRowIndex
				* metaData.iNormalHeight);
		vector2.addLocal(metaData.iWidth / 2.0f,
				-metaData.iHeight / 2.0f);
		vector2.addLocal(-fFinalMapColumn / 2.0f, fFinalMapRow / 2.0f);
		data.vectorPosition = new Vector2(vector2.x
				* fRealWorldToTileWorldRadio, vector2.y
				* fRealWorldToTileWorldRadio);
		return data;
	}

	private YSkeleton getSkeleton(YMapComponentMetaData metaData)
	{
		if (null == skeletons[metaData.iType])
			skeletons[metaData.iType] = new YRectangle(
					metaData.iWidth
							* fRealWorldToTileWorldRadio,
					metaData.iHeight
							* fRealWorldToTileWorldRadio,
					false, true);
		return skeletons[metaData.iType];
	}

	private YMapComponentMetaData[] divide(int[] i_arrDivide,
			final int iWidth, final int iHeight,
			final int iDividedColumn, final int iDividedRow)
	{
		final int iTileWidth = iWidth / iDividedColumn;
		final int iTileHeight = iHeight / iDividedRow;

		if (1 == iTileHeight
				|| 1 == iTileWidth
				|| (iWidth - iTileWidth * iDividedColumn) > iTileWidth
				|| (iHeight - iTileHeight * iDividedRow) > iTileHeight)
			return null;

		final int iNewDividedRow = iDividedRow
				+ (0 == iHeight % iTileHeight ? 0 : 1);
		final int iNewDividedCol = iDividedColumn
				+ (0 == iWidth % iTileWidth ? 0 : 1);
		YMapComponentMetaData[] res = new YMapComponentMetaData[iNewDividedCol
				* iNewDividedRow];

		for (int j = 0; j < iHeight; j++)
			for (int i = 0; i < iWidth; i++)
			{
				final int iColIndex = i / iTileWidth;
				final int iRowIndex = j / iTileHeight;
				final int iIndex = iRowIndex * iNewDividedCol
						+ iColIndex;

				if (null == res[iIndex])
				{
					res[iIndex] = createChunk(iIndex,
							iWidth, iHeight,
							iDividedColumn,
							iDividedRow,
							iTileWidth,
							iTileHeight, iColIndex,
							iRowIndex);
				}

				final int iColIndex1 = i - iColIndex
						* iTileWidth;
				final int iRowIndex1 = j - iRowIndex
						* iTileHeight;
				int iNewTileWidth = 0;

				if (iDividedColumn == iColIndex)
					// 多余出来了尾列
					iNewTileWidth = iWidth % iTileWidth;
				else
					iNewTileWidth = iTileWidth;

				final int iIndex1 = iRowIndex1 * iNewTileWidth
						+ iColIndex1;
				res[iIndex].data[iIndex1] = i_arrDivide[j
						* iWidth + i];
				res[iIndex].iNormalWidth = iTileWidth;
				res[iIndex].iNormalHeight = iTileHeight;
			}

		return res;
	}

	private YMapComponentMetaData createChunk(final int iIndex,
			final int iWidth, final int iHeight,
			final int iDividedColumn, final int iDividedRow,
			final int iTileWidth, final int iTileHeight,
			int iColIndex, int iRowIndex)
	{
		YMapComponentMetaData metaData = new YMapComponentMetaData();
		metaData.iIndex = iIndex;
		if (iDividedColumn == iColIndex)
		{// 多余出来了尾列
			if (iDividedRow == iRowIndex)
			{ // 多余出来了尾行
				metaData.iWidth = iWidth % iTileWidth;
				metaData.iHeight = iHeight % iTileHeight;
				metaData.iType = SKE_RIGHT_BOTTOM;
			} else
			{
				metaData.iWidth = iWidth % iTileWidth;
				metaData.iHeight = iTileHeight;
				metaData.iType = SKE_RIGHT;
			}

		} else
		{//
			if (iDividedRow == iRowIndex)
			{// 多余出来了尾行
				metaData.iWidth = iTileWidth;
				metaData.iHeight = iHeight % iTileHeight;
				metaData.iType = SKE_BOTTOM;
			} else
			{
				metaData.iWidth = iTileWidth;
				metaData.iHeight = iTileHeight;
				metaData.iType = SKE_NORMAL;
			}
		}

		metaData.iColIndex = iColIndex;
		metaData.iRowIndex = iRowIndex;
		metaData.data = new int[metaData.iWidth * metaData.iHeight];
		return metaData;
	}

	private static class YMapComponentMetaData
	{
		public int iRowIndex;
		public int iColIndex;
		private int[] data;
		private int iWidth;
		private int iHeight;
		private int iIndex;
		private int iType;

		private int iNormalWidth;
		private int iNormalHeight;
	}

	static class YMapComponentData
	{
		YSkeleton skeleton;
		YTileSheet tileSheet;
		YTexture textureData;
		Vector2 vectorPosition;
	}
}
