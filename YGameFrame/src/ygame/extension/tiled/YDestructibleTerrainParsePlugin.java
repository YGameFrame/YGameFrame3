package ygame.extension.tiled;

import java.util.ArrayList;

import org.jbox2d.dynamics.World;

import ygame.domain.YDomain;
import ygame.domain.YDomainView;
import ygame.extension.domain.tilemap.YTiledBean.YLayer;
import ygame.extension.domain.tilemap.YTiledBean.YObject;
import ygame.extension.domain.tilemap.YTiledBean.YPolygon;
import ygame.extension.domain.tilemap.YTiledBean.YTileSet;
import ygame.extension.program.YTextureProgram;
import ygame.extension.third_party.clipper.YPolygonClipper;
import ygame.extension.tiled.domain.YDestructibleTerrainDomain;
import ygame.extension.tiled.domain.YDestructibleTerrainLogic;
import ygame.framework.core.YABaseDomain;
import ygame.utils.YBitmapUtils;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;

public class YDestructibleTerrainParsePlugin implements YITiledParsePlugin {

	private String imgLayerName;
	private String objLayerName;
	private String key;
	private World world;

	/**
	 * 可破坏地形，目前只支持一个图层的砖块来自于一个索引图。
	 * 
	 * @param key
	 *            实体Key，生成的实体的真正Key为“Key_0”,“Key_1”等等
	 * @param imgLayerName
	 *            视图层LayerName
	 * @param objLayerName
	 *            对象层LayerName
	 * @param world
	 *            Box2D世界
	 */

	public YDestructibleTerrainParsePlugin(String key, String imgLayerName,
			String objLayerName, World world) {
		this.key = key;
		this.imgLayerName = imgLayerName;
		this.objLayerName = objLayerName;
		this.world = world;
	}

	@Override
	public void parse(YTiled tiled, float fPixelsPerUnit, Resources resources) {
		// TODO Auto-generated method stub
		YLayer objLayer = tiled.getLayerByName(objLayerName);
		YLayer imgLayer = tiled.getLayerByName(imgLayerName);
		ArrayList<ArrayList<PointF>> pointsOfPolygons = initPolygons(objLayer);
		ArrayList<Bitmap> bitmaps = initBitmaps(pointsOfPolygons, imgLayer,
				tiled);
		ArrayList<ArrayList<PointF>> pointsInWorld = toWorldCoord(
				pointsOfPolygons, tiled);
		ArrayList<YABaseDomain> domains = createDomains(pointsInWorld, bitmaps,
				resources, tiled);
		tiled.addToScene(domains.toArray(new YABaseDomain[0]));
	}

	private ArrayList<YABaseDomain> createDomains(
			ArrayList<ArrayList<PointF>> pointsInWorld,
			ArrayList<Bitmap> bitmaps, Resources resources, YTiled tiled) {
		ArrayList<YABaseDomain> domains = new ArrayList<YABaseDomain>();
		for (int i = 0; i < pointsInWorld.size(); i++) {
			YDomain domain = new YDestructibleTerrainDomain(key + "_" + i,
					new YDestructibleTerrainLogic(pointsInWorld.get(i),
							bitmaps.get(i), world, tiled), new YDomainView(
							YTextureProgram.getInstance(resources)));
			domains.add(domain);
		}
		return domains;
	}

	private ArrayList<ArrayList<PointF>> toWorldCoord(
			ArrayList<ArrayList<PointF>> pointsOfPolygons, YTiled tiled) {
		ArrayList<ArrayList<PointF>> pointsInWorld = new ArrayList<ArrayList<PointF>>();
		for (ArrayList<PointF> pointsOfPolygon : pointsOfPolygons) {
			ArrayList<PointF> pointsOfPolygonInWorld = new ArrayList<PointF>();
			for (PointF point : pointsOfPolygon) {
				PointF pointInWorld = tiled.tiledCoordToWorldCoord(point);
				pointsOfPolygonInWorld.add(pointInWorld);
			}
			pointsInWorld.add(pointsOfPolygonInWorld);
		}
		return pointsInWorld;
	}

	private ArrayList<Bitmap> initBitmaps(
			ArrayList<ArrayList<PointF>> pointsOfPolygons, YLayer imgLayer,
			YTiled tiled) {
		// TODO Auto-generated method stub
		Bitmap bitmap = createLargeBitmap(tiled, imgLayer);
		ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
		for (ArrayList<PointF> points : pointsOfPolygons) {
			RectF rect = YPolygonClipper.getBound(points);
			Bitmap bitmapForDomain = Bitmap.createBitmap(bitmap,
					(int) rect.left, (int) rect.top, (int) rect.width(),
					(int) rect.height());
			Path path = new Path();
			path.moveTo(points.get(0).x - rect.left, points.get(0).y - rect.top);
			for (int i = 1; i < points.size(); i++) {
				path.lineTo(points.get(i).x - rect.left, points.get(i).y
						- rect.top);
			}
			path.close();
			clipBitmapByPath(bitmapForDomain, path);
			bitmaps.add(bitmapForDomain);
		}
		bitmap.recycle();
		return bitmaps;
	}

	private void clipBitmapByPath(Bitmap bitmapForDomain, Path path) {
		Canvas canvas = new Canvas(bitmapForDomain);
		final Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawPath(path, paint);
	}

	private Bitmap createLargeBitmap(YTiled tiled, YLayer imgLayer) {
		int tileWidthInPixels = tiled.getTileWidthInPixels();
		int tileHeightInPixels = tiled.getTileHeightInPixels();
		int iTileColumns = tiled.getGlobalWidth();
		int iTileRows = tiled.getGlobalHeight();
		int mapWidth = iTileColumns * tileWidthInPixels;
		int mapHeight = iTileRows * tileHeightInPixels;
		Bitmap bitmap = Bitmap.createBitmap(mapWidth, mapHeight,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Bitmap indexPicture = tiled.getIndexPictureByLayer(imgLayer);
		YTileSet tileSet = tiled.getTileSetsByLayer(imgLayer);
		Rect[] peiceTiles = getPieceTiles(tileSet, indexPicture);
		Rect currentRect = new Rect();
		int[] data = imgLayer.getData();
		for (int i = 0; i < iTileRows; i++) {
			for (int j = 0; j < iTileColumns; j++) {
				int currentData = data[i * iTileColumns + j];
				if (currentData == 0) {
					continue;
				}
				int leftPoint = j * tileWidthInPixels;
				int topPoint = i * tileHeightInPixels;
				currentRect.set(leftPoint, topPoint, leftPoint
						+ tileWidthInPixels, topPoint + tileHeightInPixels);
				canvas.drawBitmap(indexPicture, peiceTiles[currentData - 1],
						currentRect, null);
			}
		}
		return bitmap;
	}

	private ArrayList<ArrayList<PointF>> initPolygons(YLayer objLayer) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<PointF>> pointsOfPolygons = new ArrayList<ArrayList<PointF>>();
		YObject[] objects = objLayer.getObjects();
		for (YObject object : objects) {
			ArrayList<PointF> points = new ArrayList<PointF>();
			float beginPpointX = object.getX();
			float beginPpointY = object.getY();
			YPolygon[] polygonPoints = object.getPolygon();
			for (YPolygon polygonPoint : polygonPoints) {
				points.add(new PointF(beginPpointX + polygonPoint.getX(),
						beginPpointY + polygonPoint.getY()));
			}
			pointsOfPolygons.add(points);
		}
		return pointsOfPolygons;
	}

	private Rect[] getPieceTiles(YTileSet tileSet, Bitmap indexPicture) {
		int iRows = tileSet.getImageheight() / tileSet.getTileheight();
		int iColumns = tileSet.getImagewidth() / tileSet.getTilewidth();
		Rect[] peiceTiles = YBitmapUtils.splitBitmapOnlyForMetaData(
				indexPicture, iColumns, iRows);
		return peiceTiles;
	}

}
