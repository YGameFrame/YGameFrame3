package ygame.math;

import ygame.math.Plane.PlaneSide;
import ygame.math.vector.Vector3;

public final class YFrustum
{
	private Vector3[] mTmp = new Vector3[8];
	protected Matrix4 mTmpMatrix = new Matrix4();
	protected static final Vector3[] mClipSpacePlanePoints =
	{ new Vector3(-1, -1, -1), new Vector3(1, -1, -1),
			new Vector3(1, 1, -1), new Vector3(-1, 1, -1),
			new Vector3(-1, -1, 1), new Vector3(1, -1, 1),
			new Vector3(1, 1, 1), new Vector3(-1, 1, 1) };

	public final Plane[] planes = new Plane[6];

	protected final Vector3[] planePoints =
	{ new Vector3(), new Vector3(), new Vector3(), new Vector3(),
			new Vector3(), new Vector3(), new Vector3(),
			new Vector3() };

	public YFrustum()
	{
		for (int i = 0; i < 6; i++)
		{
			planes[i] = new Plane(new Vector3(), 0);
		}
		for (int i = 0; i < 8; i++)
		{
			mTmp[i] = new Vector3();
		}
	}

	public void update(Matrix4 inverseProjectionView)
	{

		for (int i = 0; i < 8; i++)
		{
			planePoints[i].setAll(mClipSpacePlanePoints[i]);
			planePoints[i].project(inverseProjectionView);
		}

		planes[0].set(planePoints[1], planePoints[0], planePoints[2]);
		planes[1].set(planePoints[4], planePoints[5], planePoints[7]);
		planes[2].set(planePoints[0], planePoints[4], planePoints[3]);
		planes[3].set(planePoints[5], planePoints[1], planePoints[6]);
		planes[4].set(planePoints[2], planePoints[3], planePoints[6]);
		planes[5].set(planePoints[4], planePoints[0], planePoints[1]);
	}

	public boolean sphereInFrustum(Vector3 center, double radius)
	{
		for (int i = 0; i < planes.length; i++)
			if (planes[i].distance(center) < -radius)
				return false;

		return true;
	}

	public boolean pointInFrustum(Vector3 point)
	{
		for (int i = 0; i < planes.length; i++)
		{
			PlaneSide result = planes[i].getPointSide(point);
			if (result == PlaneSide.Back)
			{
				return false;
			}
		}
		return true;
	}

}