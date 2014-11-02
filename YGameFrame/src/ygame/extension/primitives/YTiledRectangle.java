package ygame.extension.primitives;

import ygame.skeleton.YSkeleton;

//实验失败品
class YTiledRectangle extends YSkeleton
{
	private float fTileWidth;
	private float fTileHeight;
	private int iRowNum;
	private int iColumnNum;

	private boolean bCreateColor;
	private boolean bCreateTexCoord;

	public YTiledRectangle(float fTileWidth, float fTileHeight,
			int iRowNum, int iColumnNum, boolean bCreateColor,
			boolean bCreateTexCoord)
	{
		this.fTileWidth = fTileWidth;
		this.fTileHeight = fTileHeight;
		this.iRowNum = iRowNum;
		this.iColumnNum = iColumnNum;
		this.bCreateColor = bCreateColor;
		this.bCreateTexCoord = bCreateTexCoord;
		fillData();
	}

	private void fillData()
	{
		float[] fTexCoord = getTexCoords();
		float[] fVertexPosition = getVertexPosition(fTexCoord);
		setPositions(fVertexPosition , true);
		setIndices(getIndices());

		if (bCreateTexCoord)
			setTexCoords(fTexCoord);

		if (bCreateColor)
			setColors(createRandomColorData(fVertexPosition.length / 3));
	}

	private short[] getIndices()
	{
		final int yLength = iRowNum + 1;
		final int xLength = iColumnNum + 1 ;
		// Now build the index data
		final int numStripsRequired = yLength - 1;
		final int numDegensRequired = 2 * (numStripsRequired - 1);
		final int verticesPerStrip = 2 * xLength;

		final short[] sIndices = new short[(verticesPerStrip * numStripsRequired)
				+ numDegensRequired];

		int offset = 0;

		for (int y = 0; y < yLength - 1; y++)
		{
			if (y > 0)
				// Degenerate begin: repeat first vertex
				sIndices[offset++] = (short) (y * xLength);

			for (int x = 0; x < xLength; x++)
			{
				// One part of the strip
				sIndices[offset++] = (short) ((y * xLength) + x);
				sIndices[offset++] = (short) (((y + 1) * xLength) + x);
			}

			if (y < yLength - 2)
				// Degenerate end: repeat last vertex
				sIndices[offset++] = (short) (((y + 1) * xLength) + (xLength - 1));
		}
		return sIndices;
	}

	private float[] getVertexPosition(float[] fTexCoord)
	{
		int iVertexNum = fTexCoord.length / 2;
		float[] fVertexCoord = new float[iVertexNum * 3];
		int offset = 0;
		for (int i = 0; i < fVertexCoord.length; i += 3)
		{
			fVertexCoord[i] = (fTexCoord[offset ++] - 0.5f) * iColumnNum
					* fTileWidth;
			fVertexCoord[i + 1] = (fTexCoord[offset ++] - 0.5f)
					* iRowNum * fTileHeight;
			fVertexCoord[i + 2] = 0;
		}
		return fVertexCoord;
	}

	private float[] getTexCoords()
	{
		// First, build the data for the vertex buffer
		final int yLength = iRowNum + 1;
		final int xLength = iColumnNum + 1;
		float[] fTexCoord = new float[yLength * xLength * 2];
		int offset = 0;
		for (int y = 0; y < yLength; y++)
		{
			for (int x = 0; x < xLength; x++)
			{
				final float xRatio = x / (float) (xLength - 1);

				// Build our heightmap from the top down, so
				// that our triangles are counter-clockwise.
				final float yRatio = 1f - (y / (float) (yLength - 1));

				// Position
				fTexCoord[offset++] = xRatio;
				fTexCoord[offset++] = yRatio;

			}
		}
		return fTexCoord;
	}

}
