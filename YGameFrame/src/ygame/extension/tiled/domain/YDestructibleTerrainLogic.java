package ygame.extension.tiled.domain;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import ygame.domain.YADomainLogic;
import ygame.domain.YDomain;
import ygame.extension.primitives.YRectangle;
import ygame.extension.program.YTextureProgram;
import ygame.extension.program.YTextureProgram.YAdapter;
import ygame.extension.third_party.clipper.YPolygonClipper;
import ygame.extension.tiled.YTiled;
import ygame.framework.core.YRequest;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;
import ygame.texture.YTexture;
import ygame.transformable.YMover;

public class YDestructibleTerrainLogic extends YADomainLogic {

	private YTexture texture;
	private YRectangle skeleton;
	private YMover mover;
	private Bitmap bitmap;
	private World world;
	private Body body;
	private YDestructibleTerrainDomain domainContext;
	private ArrayList<PointF> points;
	private PolyDefault polygon;
	private YTiled tiled;
	private RectF rect;
	private boolean ifReBuild;

	public YDestructibleTerrainLogic(ArrayList<PointF> points, Bitmap bitmap,
			World world, YTiled tiled) {
		rect = YPolygonClipper.getBound(points);
		this.skeleton = new YRectangle(rect.width(), rect.height(), false, true);
		this.mover = new YMover();
		mover.setX(rect.centerX()).setY(rect.centerY()).setZ(-1);
		this.bitmap = bitmap;
		this.texture = new YTexture(bitmap, false);
		this.world = world;
		this.points = points;
		this.tiled = tiled;
		ifReBuild = false;
	}

	@Override
	protected void onCycle(double dbElapseTime_s, YDomain domain,
			YWriteBundle bundle, YSystem system, YScene sceneCurrent,
			YMatrix matrix4pv, YMatrix matrix4Projection, YMatrix matrix4View) {
		// TODO Auto-generated method stub
		YTextureProgram.YAdapter adapter = (YAdapter) domainContext
				.getParametersAdapter();
		adapter.paramMatrixPV(matrix4pv).paramMover(mover)
				.paramSkeleton(skeleton).paramTexture(texture);
	}

	@Override
	protected boolean onDealRequest(YRequest request, YSystem system,
			YScene sceneCurrent, YBaseDomain domain) {
		return false;
	}

	@Override
	protected void onAttach(YSystem system, YBaseDomain domainContext) {
		// TODO Auto-generated method stub
		super.onAttach(system, domainContext);
		this.domainContext = (YDestructibleTerrainDomain) domainContext;
		this.polygon = YPolygonClipper.toPolygon(points);
		createBody(polygon);
	}

	@Override
	protected void onPreframe(YBaseDomain domainContext) {
		// TODO Auto-generated method stub
		super.onPreframe(domainContext);
		if (ifReBuild) {
			createBody(polygon);
			Body oriBody = findBody("ori_" + domainContext.KEY);
			if (oriBody != null) {
				world.destroyBody(oriBody);
			}
		}
	}

	private void createBody(PolyDefault polygon) {
		// 刷新刚体时，需要把原刚体信息更新
		Body oriBody = findBody("new_" + domainContext.KEY);
		if (oriBody != null) {
			oriBody.setUserData("ori_" + domainContext.KEY);
		}

		// 新建地图边缘刚体
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(0, 0);
		bodyDef.type = BodyType.STATIC;
		body = world.createBody(bodyDef);
		body.setUserData("new_" + domainContext.KEY);
		body.setDomain(domainContext);
		for (int i = 0; i < polygon.getNumInnerPoly(); i++) {
			int numPoints = polygon.getInnerPoly(i).getNumPoints();
			createShortEdge(polygon.getInnerPoly(i), numPoints - 1, 0);
			for (int j = 0; j < numPoints - 1; j++) {
				// 创建小段刚体
				createShortEdge(polygon.getInnerPoly(i), j, j + 1);
			}
		}
	}

	private void createShortEdge(Poly innerPoly, int pointNum_1, int pointNum_2) {
		FixtureDef fixtureDef = new FixtureDef();
		EdgeShape shape = new EdgeShape();
		Vec2 v1 = new Vec2((float) innerPoly.getX(pointNum_1),
				(float) innerPoly.getY(pointNum_1));
		Vec2 v2 = new Vec2((float) innerPoly.getX(pointNum_2),
				(float) innerPoly.getY(pointNum_2));

		shape.set(v1, v2);
		fixtureDef.shape = shape;
		body.createFixture(fixtureDef);
	}

	private Body findBody(String string) {
		// TODO Auto-generated method stub
		body = world.getBodyList();
		while (body != null) {
			if (body.getUserData() != null) {
				if (body.getUserData().equals(string)) {
					return body;
				}
			}
			body = body.getNext();
		}
		return body;
	}

	public void destroyCircle(float x, float y, float radius) {
		clipCircle(x, y, radius);
		bitmap = reDraw(bitmap, x, y, radius);
		texture.setBitmap(bitmap, false);
		ifReBuild = true;
	}

	private void clipCircle(float x, float y, float radius) {
		PolyDefault explosionPolygon = createCircle(20, new PointF(x, y),
				radius);
		polygon = (PolyDefault) polygon.difference(explosionPolygon);
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

	private Bitmap reDraw(Bitmap bitmap, float x, float y, float radius) {
		x = (x - rect.left) * tiled.getPixelsPerUnit();
		y = (rect.bottom - y) * tiled.getPixelsPerUnit();
		radius = radius * tiled.getPixelsPerUnit();
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
		canvas.drawCircle(x, y, radius, paint);
		return bitmap;
	}
}
