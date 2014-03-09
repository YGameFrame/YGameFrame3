package ygame.extension.with_third_party;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ygame.exception.YException;
import ygame.extension.YTiledJsonParser;

/**
 * <b>Tiled——box2d的json解析器</b>
 * 
 * <p>
 * <b>概述</b>：您可以利用tiled制作带有box2d刚体描述的地图，然后导出其json文件，然后利用该类将之解析
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>： 您应该将其对象层命名为“box2d_bodies”，并且可以对其刚体添加属性：<b>摩擦力</b>“friction”及<b>恢复力
 * </ b>“restitution”
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public class YTiledJson_Box2dParser extends YTiledJsonParser
{
	private static final String BOX2D_BODYS = "box2d_bodies";

	private final Vec2 OFFSET;

	private final float WIDHT_RADIO;
	private final float HEIGHT_RADIO;

	/**
	 * @param world
	 *                box2d世界
	 * @param strTiledJson
	 *                json字符串
	 * @param fRealWorldToTileWorldRadio
	 *                真实世界到瓷砖世界的比例（即一个砖块大概是多少米）
	 * @throws JSONException
	 *                 json异常
	 */
	public YTiledJson_Box2dParser(World world, String strTiledJson,
			float fRealWorldToTileWorldRadio) throws JSONException
	{
		super(strTiledJson, fRealWorldToTileWorldRadio);

		final float RADIO = fRealWorldToTileWorldRadio;
		WIDHT_RADIO = RADIO / jsonTree.getInt("tilewidth");
		HEIGHT_RADIO = RADIO / jsonTree.getInt("tileheight");

		final int iWidthInPixel = jsonTree.getInt("tilewidth")
				* jsonTree.getInt("width");
		final int iHeightInPixel = jsonTree.getInt("tileheight")
				* jsonTree.getInt("height");

		OFFSET = new Vec2(-iWidthInPixel / 2.0f, -iHeightInPixel / 2.0f);
		// JSONObject jsonProp = jsonTree.getJSONObject("properties");
		// OFFSET = new Vec2((float) (-jsonProp.optDouble("box2d_ox",
		// -iWidthInPixel / 2.0f)),
		// (float) (-jsonProp.optDouble("box2d_oy",
		// -iHeightInPixel / 2.0f)));

		parseBody(world);
	}

	private void parseBody(World world) throws JSONException
	{
		JSONObject layer = null;

		JSONArray layers = jsonTree.getJSONArray("layers");
		int iLen = layers.length();
		for (int i = 0; i < iLen; i++)
		{
			if (layers.getJSONObject(i).getString("name")
					.equals(BOX2D_BODYS))
			{
				layer = layers.getJSONObject(i);
				break;
			}
		}

		if (null == layer)
			throw new YException("地图的Box2d刚体层为空", getClass()
					.getName(), "请创建刚体层，并将其命名为："
					+ BOX2D_BODYS);

		JSONArray bodys = layer.getJSONArray("objects");
		iLen = bodys.length();
		for (int i = 0; i < iLen; i++)
			createBody(world, bodys.getJSONObject(i));
	}

	private void createBody(World world, JSONObject jsonBody)
			throws JSONException
	{
		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;
		Body body = world.createBody(bd);

		JSONArray jsonVertexs = jsonBody.optJSONArray("polygon");
		if (null != jsonVertexs)
			toPolygonBody(jsonBody, jsonVertexs, body);
		else
			toPolyLineBody(jsonBody,
					jsonBody.getJSONArray("polyline"), body);
	}

	private void toPolyLineBody(JSONObject jsonBody, JSONArray jsonVertexs,
			Body body) throws JSONException
	{
		EdgeShape shape = new EdgeShape();
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 0;
		JSONObject jsonProper = jsonBody.getJSONObject("properties");
		fd.friction = (float) jsonProper.optDouble("friction", 0);
		fd.restitution = (float) jsonProper.optDouble("restitution", 0);

		Vec2 vec2Position = new Vec2(jsonBody.getInt("x"),
				jsonBody.getInt("y"));
		Vec2 vec2Pre = new Vec2(0, 0);
		vec2Pre = tiledCoordToBox2dCoord(vec2Pre, vec2Position);
		int iLen = jsonVertexs.length();
		for (int i = 1; i < iLen; i++)
		{
			JSONObject jsonVertex = jsonVertexs.getJSONObject(i);
			Vec2 vec2Cur = new Vec2(jsonVertex.getInt("x"),
					jsonVertex.getInt("y"));
			vec2Cur = tiledCoordToBox2dCoord(vec2Cur, vec2Position);
			shape.set(vec2Pre, vec2Cur);
			body.createFixture(fd);
			vec2Pre = vec2Cur;
		}
	}

	private void toPolygonBody(JSONObject jsonBody, JSONArray jsonVertexs,
			Body body) throws JSONException
	{
		PolygonShape shape = new PolygonShape();
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 0;
		JSONObject jsonProper = jsonBody.getJSONObject("properties");
		fd.friction = (float) jsonProper.optDouble("friction", 0);
		fd.restitution = (float) jsonProper.optDouble("restitution", 0);

		Vec2 vec2Position = new Vec2(jsonBody.getInt("x"),
				jsonBody.getInt("y"));

		Vec2[] polygonVertexs = new Vec2[jsonVertexs.length()];
		for (int i = 0; i < polygonVertexs.length; i++)
		{
			JSONObject jsonVertex = jsonVertexs.getJSONObject(i);
			Vec2 vec2Tiled = new Vec2(jsonVertex.getInt("x"),
					jsonVertex.getInt("y"));
			polygonVertexs[i] = tiledCoordToBox2dCoord(vec2Tiled,
					vec2Position);
		}
		shape.set(polygonVertexs, polygonVertexs.length);
		body.createFixture(fd);
	}

	private Vec2 tiledCoordToBox2dCoord(Vec2 vec2Tiled, Vec2 vec2Position)
	{
		Vec2 vecOppo = vec2Tiled.add(vec2Position).add(OFFSET);

		vecOppo.y = -vecOppo.y;

		vecOppo.x = vecOppo.x * WIDHT_RADIO;
		vecOppo.y = vecOppo.y * HEIGHT_RADIO;
		return vecOppo;
	}
}
