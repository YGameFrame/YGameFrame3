package ygame.extension.primitives;

import ygame.skeleton.YSkeleton;

/**
 * <b>球形骨架</b>
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
public class YSphere extends YSkeleton
{
	private final float PI = (float) Math.PI;
	private float mRadius = 1;
	private int mSegmentsW = 24;
	private int mSegmentsH = 24;
	private boolean mCreateTextureCoords = true;
//	private boolean mCreateVertexColorBuffer = true;

	public YSphere(float fRadius, float[] f_arrRGBA)
	{
		this.mRadius = fRadius;
		fillData(f_arrRGBA);
	}

	private void fillData(float[] f_arrRGBA)
	{
		int numVertices = (mSegmentsW + 1) * (mSegmentsH + 1);
		int numIndices = 2 * mSegmentsW * (mSegmentsH - 1) * 3;

		float[] vertices = new float[numVertices * 3];
		float[] normals = new float[numVertices * 3];
		short[] indices = new short[numIndices];

		int i, j;
		int vertIndex = 0, index = 0;
		final float normLen = 1.0f / mRadius;

		for (j = 0; j <= mSegmentsH; ++j)
		{
			float horAngle = PI * j / mSegmentsH;
			float z = mRadius * (float) Math.cos(horAngle);
			float ringRadius = mRadius * (float) Math.sin(horAngle);

			for (i = 0; i <= mSegmentsW; ++i)
			{
				float verAngle = 2.0f * PI * i / mSegmentsW;
				float x = ringRadius
						* (float) Math.cos(verAngle);
				float y = ringRadius
						* (float) Math.sin(verAngle);

				normals[vertIndex] = x * normLen;
				vertices[vertIndex++] = x;
				normals[vertIndex] = z * normLen;
				vertices[vertIndex++] = z;
				normals[vertIndex] = y * normLen;
				vertices[vertIndex++] = y;

				if (i > 0 && j > 0)
				{
					int a = (mSegmentsW + 1) * j + i;
					int b = (mSegmentsW + 1) * j + i - 1;
					int c = (mSegmentsW + 1) * (j - 1) + i
							- 1;
					int d = (mSegmentsW + 1) * (j - 1) + i;

					if (j == mSegmentsH)
					{
						indices[index++] = (short) a;
						indices[index++] = (short) c;
						indices[index++] = (short) d;
					} else if (j == 1)
					{
						indices[index++] = (short) a;
						indices[index++] = (short) b;
						indices[index++] = (short) c;
					} else
					{
						indices[index++] = (short) a;
						indices[index++] = (short) b;
						indices[index++] = (short) c;
						indices[index++] = (short) a;
						indices[index++] = (short) c;
						indices[index++] = (short) d;
					}
				}
			}
		}

		float[] textureCoords = null;
		if (mCreateTextureCoords)
		{
			int numUvs = (mSegmentsH + 1) * (mSegmentsW + 1) * 2;
			textureCoords = new float[numUvs];

			numUvs = 0;
			for (j = 0; j <= mSegmentsH; ++j)
			{
				for (i = mSegmentsW; i >= 0; --i)
				{
					textureCoords[numUvs++] = (float) i
							/ mSegmentsW;
					textureCoords[numUvs++] = (float) j
							/ mSegmentsH;
				}
			}
		}

		float[] colors = null;

		// if (mCreateVertexColorBuffer)
		// {
		// int numColors = numVertices * 4;
		// colors = new float[numColors];
		// for (j = 0; j < numColors; j += 4)
		// {
		// // colors[j] = 1.0f;
		// // colors[j + 1] = 0;
		// // colors[j + 2] = 0;
		// // colors[j + 3] =
		// // 1.0f;
		//
		// colors[j] = 0;
		// colors[j + 1] = 0;
		// colors[j + 2] = 1;
		// colors[j + 3] = 1.0f;
		// }
		// }
		if (null == f_arrRGBA)
			colors = createRandomColorData(vertices.length / 3);
		else
			colors = createColorData(vertices.length / 3, f_arrRGBA);

		setPositions(vertices , true);
		setNormals(normals);
		setTexCoords(textureCoords);
		setColors(colors);
		setIndices(indices);

		vertices = null;
		normals = null;
		textureCoords = null;
		indices = null;
	}
}
