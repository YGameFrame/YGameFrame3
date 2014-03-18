package ygame.texture;

import android.content.res.Resources;
import android.graphics.Bitmap;

/**
 * <b>瓷砖图</b>
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
public class YTileSheet extends YTexture
{
	/** <b>行数</b> */
	public final int ROW_NUM;
	/** <b>列数</b> */
	public final int COLUMN_NUM;

	/** <b>单格宽度</b>：总宽为1.0 */
	public final float GRID_WIDTH;
	/** <b>单格高度</b>：总高为1.0 */
	public final float GRID_HEIGHT;

	/**
	 * @param iResId
	 *                位图资源标识
	 * @param resources
	 *                资源
	 * @param iRowNums
	 *                行数
	 * @param iColumnNums
	 *                列数
	 */
	public YTileSheet(int iResId, Resources resources, int iRowNums,
			int iColumnNums)
	{
		super(iResId, resources);
		this.COLUMN_NUM = iColumnNums;
		this.ROW_NUM = iRowNums;

		this.GRID_WIDTH = 1.0f / COLUMN_NUM;
		this.GRID_HEIGHT = 1.0f / ROW_NUM;
	}

	public YTileSheet(Bitmap bitmap, int iRowNums, int iColumnNums)
	{
		super(bitmap);
		this.COLUMN_NUM = iColumnNums;
		this.ROW_NUM = iRowNums;

		this.GRID_WIDTH = 1.0f / COLUMN_NUM;
		this.GRID_HEIGHT = 1.0f / ROW_NUM;
	}

}
