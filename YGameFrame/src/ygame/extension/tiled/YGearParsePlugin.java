package ygame.extension.tiled;

import java.util.HashMap;
import java.util.Map;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import ygame.domain.YADomainLogic;
import ygame.domain.YDomain;
import ygame.domain.YDomainView;
import ygame.exception.YException;
import ygame.extension.domain.YPolyLineDomain;
import ygame.extension.domain.tilemap.YTiledBean.YLayer;
import ygame.extension.domain.tilemap.YTiledBean.YObject;
import ygame.extension.primitives.YRectangle;
import ygame.extension.program.YSimpleProgram;
import ygame.extension.program.YSimpleProgram.YAdapter;
import ygame.extension.with_third_party.YWorld;
import ygame.framework.core.YABaseDomain;
import ygame.framework.core.YRequest;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YWriteBundle;
import ygame.math.MathUtils;
import ygame.math.YMatrix;
import ygame.math.vector.Vector3;
import ygame.skeleton.YSkeleton;
import ygame.transformable.YMover;
import android.content.res.Resources;
import android.graphics.PointF;

public class YGearParsePlugin implements YITiledParsePlugin
{
	public static final String REVOLUTE_JOINT = "revolute_joint";
	public static final String DISTANCE_JOINT = "distance_joint";

	public static final String STATIC_BODY = "static_body";
	public static final String DYNAMIC_BODY = "dynamic_body";

	private static final String TAG = YGearParsePlugin.class
			.getSimpleName();

	private String[] layerNames;
	private YWorld world;

	private Resources resources;

	public YGearParsePlugin(Resources resources, YWorld world,
			String... layerNames)
	{
		this.layerNames = layerNames;
		this.world = world;
		this.resources = resources;
	}

	@Override
	public void parse(YTiled tiled, float fPixelsPerUnit,
			Resources resources)
	{
		Map<YObject, Body> mapBody = new HashMap<YObject, Body>();
		for (String layerName : layerNames)
		{
			YLayer layer = tiled.getLayerByName(layerName);
			if (!YLayer.TYPE_OBJECT_LAYER.equals(layer.getType()))
				throw new YException("只能解析物理层", TAG, "");
			YObject[] objects = layer.getObjects();
			for (YObject obj : objects)
			{
				//@formatter:off
					if (REVOLUTE_JOINT.equals(obj.getType()))
					{
						createRevoluteJointAndBodyIfNeed(
								tiled,
								fPixelsPerUnit,
								mapBody,
								objects, obj);
					}else if(DISTANCE_JOINT.equals(obj.getType()))
					{
						createDistanceJointAndBodyIfNeed(
								tiled,
								fPixelsPerUnit,
								mapBody,
								objects, obj);
					}
					//@formatter:on
			}
		}
	}

	private void createDistanceJointAndBodyIfNeed(YTiled tiled,
			float fPixelsPerUnit,
			Map<YObject, Body> bodiesGenerated, YObject[] objects,
			YObject objJoint)
	{
		String jointName = objJoint.getName();
		String[] bodyNames = jointName.split("\\:");
		YObject objA = getObjectByName(bodyNames[0], objects);
		YObject objB = getObjectByName(bodyNames[1], objects);

		Body bodyA = bodiesGenerated.get(objA);
		if (null == bodyA)
		{
			bodyA = createBody(world, objA, tiled);
			bodiesGenerated.put(objA, bodyA);
			tiled.addToScene(createDomain(bodyA, objA,
					fPixelsPerUnit));
		}
		Body bodyB = bodiesGenerated.get(objB);
		if (null == bodyB)
		{
			bodyB = createBody(world, objB, tiled);
			bodiesGenerated.put(objB, bodyB);
			tiled.addToScene(createDomain(bodyB, objB,
					fPixelsPerUnit));
		}

		DistanceJointDef jd = new DistanceJointDef();
		Vec2 jointPos = new Vec2(objJoint.getX(), objJoint.getY());
		Vec2 anchorA = tiled.tiledCoordToWorldCoord(
				new Vec2(objJoint.getPolyline()[0].getX(),
						objJoint.getPolyline()[0]
								.getY()),
				jointPos);
		Vec2 anchorB = tiled.tiledCoordToWorldCoord(
				new Vec2(objJoint.getPolyline()[1].getX(),
						objJoint.getPolyline()[1]
								.getY()),
				jointPos);

		jd.initialize(bodyA, bodyB, anchorA, anchorB);
		Joint joint = world.createJoint(jd);
		tiled.addToScene(createJointDomain(jointName, joint));
	}

	private YABaseDomain createJointDomain(String jointName,
			final Joint joint)
	{
		return new YPolyLineDomain(jointName, resources)
		{
			private Vec2 anchorA = new Vec2();
			private Vec2 anchorB = new Vec2();

			@Override
			protected void onClockCycle(double dbElapseTime_s,
					YSystem system, YScene sceneCurrent,
					YMatrix matrix4pv,
					YMatrix matrix4Projection,
					YMatrix matrix4View)
			{
				super.onClockCycle(dbElapseTime_s, system,
						sceneCurrent, matrix4pv,
						matrix4Projection, matrix4View);
				joint.getAnchorA(anchorA);
				joint.getAnchorB(anchorB);
				show(anchorA.x, anchorA.y, 0.03f, anchorB.x,
						anchorB.y, 0.03f);
			}
		};
	}

	private void createRevoluteJointAndBodyIfNeed(YTiled tiled,
			float fPixelsPerUnit,
			Map<YObject, Body> bodiesGenerated, YObject[] objects,
			YObject objJoint)
	{
		String jointName = objJoint.getName();
		String[] bodyNames = jointName.split("\\:");
		YObject objA = getObjectByName(bodyNames[0], objects);
		YObject objB = getObjectByName(bodyNames[1], objects);

		Body bodyA = bodiesGenerated.get(objA);
		if (null == bodyA)
		{
			bodyA = createBody(world, objA, tiled);
			bodiesGenerated.put(objA, bodyA);
			tiled.addToScene(createDomain(bodyA, objA,
					fPixelsPerUnit));
		}
		Body bodyB = bodiesGenerated.get(objB);
		if (null == bodyB)
		{
			bodyB = createBody(world, objB, tiled);
			bodiesGenerated.put(objB, bodyB);
			tiled.addToScene(createDomain(bodyB, objB,
					fPixelsPerUnit));
		}

		RevoluteJointDef jd = new RevoluteJointDef();
		PointF jointPoint = tiled.tiledCoordToWorldCoord(new PointF(
				objJoint.getX(), objJoint.getY()));
		jd.initialize(bodyA, bodyB,
				new Vec2(jointPoint.x, jointPoint.y));
		world.createJoint(jd);
	}

	private YObject getObjectByName(String bodyName, YObject[] objects)
	{
		for (YObject obj : objects)
			if (obj.getName().equals(bodyName))
				return obj;
		throw new YException("没有找到刚体：" + bodyName, TAG, "");
	}

	private Body createBody(World world, YObject object, YTiled tiled)
	{
		//@formatter:off
			BodyDef def = new BodyDef();
			def.position = tiled.tiledCoordToWorldCoord(
					new Vec2(object.getWidth() / 2,object.getHeight() / 2),
					new Vec2(object.getX(),object.getY()));
			def.type = STATIC_BODY.equals(object.getType()) ? BodyType.STATIC
					: BodyType.DYNAMIC;
			Body body = world.createBody(def);
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(
					object.getWidth() / 2/ tiled.getPixelsPerUnit(),
					object.getHeight()/ 2/ tiled.getPixelsPerUnit());
			//@formatter:on
		body.createFixture(shape, 1);
		return body;
	}

	private YDomain createDomain(final Body body, final YObject object,
			final float fPixelsPerUnit)
	{
		YADomainLogic logic = new YADomainLogic()
		{

			private YMover mover = (YMover) new YMover()
					.setShaft(new Vector3(0, 0, 1));
			private YSkeleton skeleton = new YRectangle(
					object.getWidth() / fPixelsPerUnit,
					object.getHeight() / fPixelsPerUnit,
					true, false);

			@Override
			protected boolean onDealRequest(YRequest request,
					YSystem system, YScene sceneCurrent,
					YBaseDomain domainContext)
			{
				return false;
			}

			@Override
			protected void onCycle(double dbElapseTime_s,
					YDomain domainContext,
					YWriteBundle bundle, YSystem system,
					YScene sceneCurrent, YMatrix matrix4pv,
					YMatrix matrix4Projection,
					YMatrix matrix4View)
			{
				Vec2 pos = body.getPosition();
				mover.setX(pos.x)
						.setY(pos.y)
						.setAngle(body.getAngle()
								* MathUtils.RAD2DEG);
				YSimpleProgram.YAdapter adapter = (YAdapter) domainContext
						.getParametersAdapter();
				adapter.paramMatrixPV(matrix4pv)
						.paramMover(mover)
						.paramSkeleton(skeleton);
			}
		};
		YDomainView view = new YDomainView(
				YSimpleProgram.getInstance(resources));
		return new YDomain(object.getName(), logic, view);
	}
}