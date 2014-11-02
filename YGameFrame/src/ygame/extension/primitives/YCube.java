package ygame.extension.primitives;

import ygame.skeleton.YSkeleton;

public class YCube extends YSkeleton
{

	private float mSize;
	private boolean mCreateTextureCoords = true;
	private boolean mCreateVertexColorBuffer = true;

	public YCube(float fSideLength , float[] f_arrRGBA)
	{
		mSize = fSideLength;
		fillData(f_arrRGBA);
	}

	private void fillData(float[] f_arrRGBA)
	{
		float halfSize = mSize * .5f;
		float[] vertices =
		{
			// -- back
			halfSize, halfSize, halfSize, 			-halfSize, halfSize, halfSize,
			-halfSize, -halfSize, halfSize,			halfSize, -halfSize, halfSize, // 0-1-halfSize-3 front
			
			halfSize, halfSize, halfSize, 			halfSize, -halfSize, halfSize, 
			halfSize, -halfSize, -halfSize, 		halfSize, halfSize, -halfSize,// 0-3-4-5 right
			// -- front
			halfSize, -halfSize, -halfSize, 		-halfSize, -halfSize, -halfSize, 
			-halfSize, halfSize, -halfSize,			halfSize, halfSize, -halfSize,// 4-7-6-5 back
			
			-halfSize, halfSize, halfSize, 			-halfSize, halfSize, -halfSize, 
			-halfSize, -halfSize, -halfSize,		-halfSize,	-halfSize, halfSize,// 1-6-7-halfSize left
			
			halfSize, halfSize, halfSize, 			halfSize, halfSize, -halfSize, 
			-halfSize, halfSize, -halfSize, 		-halfSize, halfSize, halfSize, // top
			
			halfSize, -halfSize, halfSize, 			-halfSize, -halfSize, halfSize, 
			-halfSize, -halfSize, -halfSize,		halfSize, -halfSize, -halfSize,// bottom
		};

		float[] textureCoords = null;

		if (mCreateTextureCoords)
		{
			textureCoords = new float[]
			{ 0, 1, 1, 1, 1, 0, 0, 0, // front
					0, 1, 1, 1, 1, 0, 0, 0, // up
					0, 1, 1, 1, 1, 0, 0, 0, // back
					0, 1, 1, 1, 1, 0, 0, 0, // down
					0, 1, 1, 1, 1, 0, 0, 0, // right
					0, 1, 1, 1, 1, 0, 0, 0, // left
			};
		}

		float[] colors = null;
//		if (mCreateVertexColorBuffer)
		if(null == f_arrRGBA)
			colors = createRandomColorData(vertices.length / 3);
		else
			colors = createColorData(vertices.length / 3, f_arrRGBA);

		float n = 1;

		float[] normals =
		{ 0, 0, n, 0, 0, n, 0, 0, n, 0, 0, n, // front
				n, 0, 0, n, 0, 0, n, 0, 0, n, 0, 0, // right
				0, 0, -n, 0, 0, -n, 0, 0, -n, 0, 0, -n, // back
				-n, 0, 0, -n, 0, 0, -n, 0, 0, -n, 0, 0, // left
				0, n, 0, 0, n, 0, 0, n, 0, 0, n, 0, // top
				0, -n, 0, 0, -n, 0, 0, -n, 0, 0, -n, 0, // bottom
		};

		short[] indices =
		{ 0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12,
				13, 14, 12, 14, 15, 16, 17, 18, 16, 18, 19, 20,
				21, 22, 20, 22, 23, };
		
		setIndices(indices);
		setNormals(normals);
		setPositions(vertices , true);
		if (mCreateVertexColorBuffer)
			setColors(colors);
		if (mCreateTextureCoords)
			setTexCoords(textureCoords);

		vertices = null;
		normals = null;
		textureCoords = null;
		colors = null;
		indices = null;
	}
}
