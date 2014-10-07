package ygame.extension.domain.tilemap;

import java.util.ArrayList;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import com.seisw.util.geom.Point2D;

import ygame.domain.YABaseShaderProgram.YABaseParametersAdapter;
import ygame.domain.YADomainLogic;
import ygame.domain.YDomain;
import ygame.domain.YDomainView;
import ygame.extension.domain.tilemap.YTiledBean.YLayer;
import ygame.extension.domain.tilemap.YTiledBean.YObject;
import ygame.extension.domain.tilemap.YTiledBean.YPolygon;
import ygame.extension.domain.tilemap.YTiledBean.YTileSet;
import ygame.extension.primitives.YRectangle;
import ygame.extension.program.YTextureProgram;
import ygame.extension.program.YTextureProgram.YAdapter;
import ygame.extension.third_party.clipper.YPolygonClipper;
import ygame.framework.core.YABaseDomain;
import ygame.framework.core.YRequest;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;
import ygame.skeleton.YSkeleton;
import ygame.texture.YTexture;
import ygame.transformable.YMover;
import ygame.utils.YBitmapUtils;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * 可破坏地形
 * @author wangzi6147
 *
 */
public class YDestructibleTerrain extends YATileMapDomain {

	private static final int DEFAULT_PIXELS_PER_UNIT = 32;
	private Bitmap bitmap;
	private Resources resources;
	private int mapWidth;
	private int mapHeight;
	private Bitmap tilesBitmap;
	private YPolygonClipper polygonClipper;
	private World world;
	private YDomain domain;
	public boolean ifRedraw;
	private float pixelsPerUnit;

	protected YDestructibleTerrain(String KEY, String jsonFileNameInAsset,
			Context context, World world) {
		super(KEY, jsonFileNameInAsset, context);
		this.world = world;
		resources = context.getResources();
	}

	@Override
	protected void onAttach(YSystem system) {
		// TODO Auto-generated method stub
		super.onAttach(system);

		ifRedraw = false;
		pixelsPerUnit = confirmPixelsPerUnit();
		initMap();
		YRectangle skeleton = new YRectangle((float) mapWidth / pixelsPerUnit,
				(float) mapHeight / pixelsPerUnit, false, true);
		ArrayList<YABaseDomain> domains = new ArrayList<YABaseDomain>();
		domain = new YDomain(KEY, new YMapLogic(skeleton, new YTexture(bitmap,
				false)),
				new YDomainView(YTextureProgram.getInstance(resources)));
		polygonClipper.createEdgeBody(world, domain);
		domains.add(domain);
		addComponentDomains(domains);
	}

	private float confirmPixelsPerUnit() {
		float fPixelsPerUnit = tiledBean.getProperties().getPixels_per_unit();
		fPixelsPerUnit = fPixelsPerUnit <= 0 ? DEFAULT_PIXELS_PER_UNIT
				: fPixelsPerUnit;
		return fPixelsPerUnit;
	}

	private void initMap() {
		mapWidth = tiledBean.getWidth() * tiledBean.getTilewidth();
		mapHeight = tiledBean.getHeight() * tiledBean.getTileheight();

		bitmap = Bitmap.createBitmap(mapWidth, mapHeight, Config.ARGB_8888);
		YLayer[] layers = tiledBean.getLayers();
		for (int i = 0; i < layers.length; i++) {
			if (layers[i].getType().equals("tilelayer")) {
				createTileLayer(layers[i], tiledBean);
			} else if (layers[i].getType().equals("objectgroup")) {
				createPolygon(layers[i]);
			}
		}
	}

	private void createPolygon(YLayer layer) {
		// TODO Auto-generated method stub
		ArrayList<PointF> points = new ArrayList<PointF>();
		YObject[] objects = layer.getObjects();
		YPolygon[] polygons = objects[0].getPolygon();
		PointF beginPpoint = new PointF();
		beginPpoint.x = (float) objects[0].getX() - (float) mapWidth / 2;
		beginPpoint.y = -(float) objects[0].getY() + (float) mapHeight / 2;
		for (int i = 0; i < polygons.length; i++) {
			points.add(new PointF((float) (beginPpoint.x + polygons[i].getX())
					/ pixelsPerUnit, (float) (beginPpoint.y - polygons[i]
					.getY()) / pixelsPerUnit));
		}
		polygonClipper = new YPolygonClipper(points);
	}

	/**
	 * 可以对地图实体进行破坏（仅限圆形爆炸区域）
	 * @param x 爆炸中心x
	 * @param y 爆炸中心y
	 * @param radius 爆炸半径
	 */
	public void destroy(float x, float y, float radius) {
		polygonClipper.clipCircle(x, y, radius, domain);
		bitmap = reDraw(bitmap, x, y, radius);
		ifRedraw = true;
	}

	private Bitmap reDraw(Bitmap oriMap, float x, float y, float radius) {
		x = x * pixelsPerUnit + (float) mapWidth / 2;
		y = -y * pixelsPerUnit + (float) mapHeight / 2;
		radius = radius * pixelsPerUnit;
		Canvas canvas = new Canvas(oriMap);
		final Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
		canvas.drawCircle(x, y, radius, paint);
		return oriMap;
	}

	private void createTileLayer(YLayer layer, YTiledBean tiledBean) {
		// TODO Auto-generated method stub
		Canvas canvas = new Canvas(bitmap);
		YTileSet[] tilesets = tiledBean.getTilesets();
		Rect[] peiceTiles = getPieceTiles(tilesets[0]);
		Rect currentRect = new Rect();
		int iTileColumns = tiledBean.getWidth();
		int iTileRows = tiledBean.getHeight();
		int[] data = layer.getData();
		for (int i = 0; i < iTileRows; i++) {
			for (int j = 0; j < iTileColumns; j++) {
				int currentData = data[i * iTileColumns + j];
				if (currentData == 0) {
					continue;
				}
				int leftPoint = j * tiledBean.getTilewidth();
				int topPoint = i * tiledBean.getTileheight();
				currentRect.set(leftPoint, topPoint,
						leftPoint + tiledBean.getTilewidth(), topPoint
								+ tiledBean.getTileheight());
				canvas.drawBitmap(tilesBitmap, peiceTiles[currentData - 1],
						currentRect, null);
			}
		}

	}

	private Rect[] getPieceTiles(YTileSet tileset) {
		tilesBitmap = getBitmapByName(tileset.getName());
		int iRows = tileset.getImageheight() / tileset.getTileheight();
		int iColumns = tileset.getImagewidth() / tileset.getTilewidth();
		Rect[] peiceTiles = YBitmapUtils.splitBitmapOnlyForMetaData(
				tilesBitmap, iColumns, iRows);
		return peiceTiles;
	}

	private class YMapLogic extends YADomainLogic {

		private YTexture texture;
		private YRectangle skeleton;
		private YMover mover = new YMover();

		public YMapLogic(YRectangle skeleton, YTexture texture) {
			// TODO Auto-generated constructor stub
			this.skeleton = skeleton;
			this.texture = texture;
			mover.setZ(-1);
		}

		@Override
		protected void onCycle(double dbElapseTime_s, YDomain domainContext,
				YWriteBundle bundle, YSystem system, YScene sceneCurrent,
				YMatrix matrix4pv, YMatrix matrix4Projection,
				YMatrix matrix4View) {
			// TODO Auto-generated method stub
			if (ifRedraw) {
				texture.setBitmap(bitmap, false);
				ifRedraw = false;
			}
			YTextureProgram.YAdapter adapter = (YAdapter) domainContext
					.getParametersAdapter();
			adapter.paramMatrixPV(matrix4pv).paramMover(mover)
					.paramSkeleton(skeleton).paramTexture(texture);
		}

		@Override
		protected boolean onDealRequest(YRequest request, YSystem system,
				YScene sceneCurrent, YBaseDomain domainContext) {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
