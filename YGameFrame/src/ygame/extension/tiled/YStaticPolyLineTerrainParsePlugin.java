package ygame.extension.tiled;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import ygame.exception.YException;
import ygame.extension.domain.tilemap.YTiledBean;
import ygame.extension.domain.tilemap.YTiledBean.YLayer;
import ygame.extension.domain.tilemap.YTiledBean.YObject;
import ygame.extension.domain.tilemap.YTiledBean.YPolygon;
import android.content.res.Resources;

public class YStaticPolyLineTerrainParsePlugin implements YITiledParsePlugin
{
	private static final String TAG = YStaticPolyLineTerrainParsePlugin.class
			.getSimpleName();
	private String[] layerNames;
	private World world;

	public YStaticPolyLineTerrainParsePlugin(World world,
			String... layerNames)
	{
		this.layerNames = layerNames;
		this.world = world;
	}

	@Override
	public void parse(YTiled tiled, float fPixelsPerUnit,
			Resources resources)
	{
		final int iWidthInPixel = tiled.getTileWidthInPixels()
				* tiled.getGlobalWidth();
		final int iHeightInPixel = tiled.getTileHeightInPixels()
				* tiled.getGlobalHeight();

		final Vec2 OFFSET = new Vec2(-iWidthInPixel / 2.0f,
				-iHeightInPixel / 2.0f);
		for (String layerName : layerNames)
		{
			YLayer layer = tiled.getLayerByName(layerName);
			if (YTiledBean.YLayer.TYPE_OBJECT_LAYER.equals(layer
					.getType()))
				parseLayer(layer, tiled, OFFSET, fPixelsPerUnit);
			else
				throw new YException(TAG + "不能解析类型为"
						+ layer.getType()
						+ "的图层，只能解析对象图层", TAG, "");
		}
		tiled.addToScene();
	}

	private void parseLayer(YLayer layer, YTiled tiled, Vec2 offset,
			float fPixelsPerUnit)
	{
		YObject[] objects = layer.getObjects();
		for (YObject object : objects)
			createBody(world, object, offset, fPixelsPerUnit);
	}

	private void createBody(World world, YObject object, Vec2 offset,
			float fPixelsPerUnit)
	{
		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;
		Body body = world.createBody(bd);
		toPolyLineBody(body, object, offset, fPixelsPerUnit);
	}

	private void toPolyLineBody(Body body, YObject object, Vec2 offset,
			float fPixelsPerUnit)
	{
		EdgeShape shape = new EdgeShape();
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 0;
//		fd.friction = object.getProperties().getFriction();
//		fd.restitution = object.getProperties().getRestitution();

		Vec2 vec2Position = new Vec2(object.getX(), object.getY());
		Vec2 vec2Pre = new Vec2(0, 0);
		vec2Pre = tiledCoordToBox2dCoord(vec2Pre, vec2Position, offset,
				fPixelsPerUnit);
		YPolygon[] polyline = object.getPolyline();
		for (int i = 1; i < polyline.length; i++)
		{
			Vec2 vec2Cur = new Vec2(polyline[i].getX(),
					polyline[i].getY());
			vec2Cur = tiledCoordToBox2dCoord(vec2Cur, vec2Position,
					offset, fPixelsPerUnit);
			shape.set(vec2Pre, vec2Cur);
			body.createFixture(fd);
			vec2Pre = vec2Cur;
		}
	}

	private Vec2 tiledCoordToBox2dCoord(Vec2 vec2Tiled, Vec2 vec2Position,
			Vec2 offset, float fPixelsPerUnit)
	{
		Vec2 vecOppo = vec2Tiled.add(vec2Position).add(offset);

		vecOppo.y = -vecOppo.y;

		vecOppo.x = vecOppo.x / fPixelsPerUnit;
		vecOppo.y = vecOppo.y / fPixelsPerUnit;
		return vecOppo;
	}

}
