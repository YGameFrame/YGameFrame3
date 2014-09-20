package ygame.utils;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Rect;

public final class YBitmapUtils
{
	private YBitmapUtils()
	{
	}

	/**
	 * 拆分给定位图对象
	 * 
	 * @param bigBmp
	 *                要拆分的位图
	 * @param horizonTileNum
	 *                水平方向上拆分的块数
	 * @param verticalTileNum
	 *                竖直方向上拆分的块数
	 * @param bRecycleBigBmp
	 *                真则回收{@code bigBmp}，反之不回收
	 * @return 拆分后的小图数组
	 */
	public static Bitmap[] splitBitmap(Bitmap bigBmp, int horizonTileNum,
			int verticalTileNum, boolean bRecycleBigBmp)
	{
		// xPiece 要分的行数 ，yPiece 要分得列数 pieceWidth每个图片的宽度
		// pieceHeight每个图片的高度
		final int xPiece = horizonTileNum;
		final int yPiece = verticalTileNum;
		final int pieceWidth = bigBmp.getWidth() / xPiece;
		final int pieceHeight = bigBmp.getHeight() / yPiece;
		ArrayList<Bitmap> pieces = new ArrayList<Bitmap>(xPiece
				* yPiece);
		for (int i = 0; i < yPiece; i++)
			for (int j = 0; j < xPiece; j++)
			{
				int xValue = j * pieceWidth;
				int yValue = i * pieceHeight;
				Bitmap piece = Bitmap.createBitmap(bigBmp,
						xValue, yValue, pieceWidth,
						pieceHeight);
				pieces.add(piece);
			}

		if (bRecycleBigBmp)
			bigBmp.recycle();

		return pieces.toArray(new Bitmap[0]);
	}

	public static Rect[] splitBitmapOnlyForMetaData(Bitmap bigBmp,
			int horizonTileNum, int verticalTileNum)
	{
		// xPiece 要分的行数 ，yPiece 要分得列数 pieceWidth每个图片的宽度
		// pieceHeight每个图片的高度
		final int xPiece = horizonTileNum;
		final int yPiece = verticalTileNum;
		final int pieceWidth = bigBmp.getWidth() / xPiece;
		final int pieceHeight = bigBmp.getHeight() / yPiece;
		ArrayList<Rect> pieces = new ArrayList<Rect>(xPiece * yPiece);
		for (int i = 0; i < yPiece; i++)
			for (int j = 0; j < xPiece; j++)
			{
				int xValue = j * pieceWidth;
				int yValue = i * pieceHeight;
				Rect piece = new Rect(xValue, yValue, xValue
						+ pieceWidth, yValue
						+ pieceHeight);
				pieces.add(piece);
			}

		return pieces.toArray(new Rect[0]);
	}

}
