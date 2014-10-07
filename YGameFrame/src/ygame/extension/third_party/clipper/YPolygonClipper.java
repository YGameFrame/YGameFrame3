package ygame.extension.third_party.clipper;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import ygame.framework.core.YABaseDomain;
import ygame.framework.domain.YBaseDomain;

import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;

import android.graphics.Point;
import android.graphics.PointF;
/**
 * 基于第三方库General Polygon Clipper Library
 * @author wangzi6147
 *
 */
public class YPolygonClipper {

	private PolyDefault polygon;
	private BodyDef bodyDef;
	private Body body;
	private FixtureDef fixtureDef;
	private EdgeShape shape;
	private World world;

	public YPolygonClipper() {
		// TODO Auto-generated constructor stub
	}

	public YPolygonClipper(ArrayList<PointF> points) {
		polygon = new PolyDefault();
		for (int i = 0; i < points.size(); i++) {
			polygon.add(points.get(i).x, points.get(i).y);
		}
	}

	public void clipCircle(float x, float y, float radius, YBaseDomain domainContext) {
		PolyDefault explosionPolygon = createCircle(20, new PointF(x, y),
				radius);
		polygon = (PolyDefault) polygon.difference(explosionPolygon);
		createEdgeBody(world, domainContext);
		Body oriBody = findBody("oriTerrain", world);
		if (oriBody != null) {
			while(world.isLocked()){
				continue;
			}
			world.destroyBody(oriBody);
		}
	}

	private PolyDefault createCircle(int polyNum, PointF centerPoint,
			float radius) {
		// TODO Auto-generated method stub
		double angle = 2 * Math.PI / polyNum;
		PolyDefault circlePolygon = new PolyDefault();
		for (int i = 0; i < polyNum; i++) {
			float x = (float) (centerPoint.x + radius * Math.cos(angle * i));
			float y = (float) (centerPoint.y + radius * Math.sin(angle * i));
			circlePolygon.add(x, y);
		}
		return circlePolygon;
	}

	public void createEdgeBody(World world, YABaseDomain domainContext) {
		this.world = world;

		// 刷新刚体时，需要把原刚体信息更新
		Body oriBody = findBody("newTerrain", world);
		if (oriBody != null) {
			oriBody.setUserData("oriTerrain");
		}

		// 新建地图边缘刚体
		bodyDef = new BodyDef();
		bodyDef.position.set(0, 0);
		bodyDef.type = BodyType.STATIC;
		while(world.isLocked()){
			continue;
		}
		body = world.createBody(bodyDef);
		body.setUserData("newTerrain");
		body.setDomain(domainContext);
		
		fixtureDef = new FixtureDef();
		shape = new EdgeShape();
		for (int i = 0; i < polygon.getNumInnerPoly(); i++) {

			int numPoints = polygon.getInnerPoly(i).getNumPoints();
			createShortEdge(polygon.getInnerPoly(i), numPoints - 1, 0);
			for (int j = 0; j < numPoints - 1; j++) {
				// 创建小段刚体
				createShortEdge(polygon.getInnerPoly(i), j, j + 1);
			}
		}
	}

	private Body findBody(String string, World world) {
		// TODO Auto-generated method stub
		body = world.getBodyList();
		while (body != null) {
			if (body.getUserData().equals(string)) {
				return body;
			}
			body = body.getNext();
		}
		return body;
	}

	private void createShortEdge(Poly innerPoly, int pointNum_1, int pointNum_2) {
		// TODO Auto-generated method stub
		Vec2 v1 = new Vec2((float) innerPoly.getX(pointNum_1),
				(float) innerPoly.getY(pointNum_1));
		Vec2 v2 = new Vec2((float) innerPoly.getX(pointNum_2),
				(float) innerPoly.getY(pointNum_2));

		shape.set(v1, v2);
		fixtureDef.shape = shape;
		while(world.isLocked()){
			continue;
		}
		body.createFixture(fixtureDef);
	}
}
