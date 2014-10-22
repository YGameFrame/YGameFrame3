package ygame.extension.tiled;

import org.jbox2d.common.Vec2;

import ygame.exception.YException;
import ygame.extension.domain.tilemap.YTiledBean.YLayer;
import ygame.extension.domain.tilemap.YTiledBean.YObject;
import ygame.framework.core.YABaseDomain;
import android.content.res.Resources;

public class YBaseParsePlugin implements YITiledParsePlugin
{
	private static String TAG = YBaseParsePlugin.class.getSimpleName();

	private String[] layerNames;
	private Object[] extraParams;

	public YBaseParsePlugin(Object[] extraParams, String... layerNames)
	{
		this.layerNames = layerNames;
		this.extraParams = extraParams;
	}

	@Override
	public final void parse(YTiled tiled, float fPixelsPerUnit,
			Resources resources)
	{
		for (String layerName : layerNames)
		{
			YLayer layer = tiled.getLayerByName(layerName);
			YObject[] objects = layer.getObjects();
			for (YObject obj : objects)
				try
				{
					YABaseDomain domain = parseDomain(obj,
							fPixelsPerUnit, tiled);
					tiled.addToScene(domain);
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					throw new YException("不能加载构建实体建造器类",
							TAG, "");
				} catch (InstantiationException e)
				{
					e.printStackTrace();
					throw new YException(e);
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
					throw new YException(e);
				}
		}
		// extraParams不过是为了传参给解析使用而已，解析完成后释放内存
		extraParams = null;
	}

	protected YABaseDomain parseDomain(YObject object,
			float fPixelsPerUnit, YTiled tiled)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		final String builderClassName = object.getType();
		if (null == builderClassName
				|| "".equals(builderClassName.trim()))
			throw new YException(
					"Tiled中设计该实体时需要添加字段-类型（type），值为domainBuilderClassName",
					TAG, "");

		Class<?> clazzBuilder = Class.forName(builderClassName);
		Object objBuilder = clazzBuilder.newInstance();
		if (objBuilder instanceof YIDomainBuilder)
		{
			YIDomainBuilder builder = (YIDomainBuilder) objBuilder;
			Vec2 vec2TiledRectCenter = new Vec2(object.getX()
					+ object.getWidth() / 2, object.getY()
					+ object.getHeight() / 2);

			final int iWidthInPixel = tiled.getTileWidthInPixels()
					* tiled.getGlobalWidth();
			final int iHeightInPixel = tiled
					.getTileHeightInPixels()
					* tiled.getGlobalHeight();
			final Vec2 OFFSET = new Vec2(-iWidthInPixel / 2.0f,
					-iHeightInPixel / 2.0f);
			Vec2 vec2WorldRectCenter = tiledCoordToBox2dCoord(
					vec2TiledRectCenter, OFFSET,
					fPixelsPerUnit);
			YDomainBuildInfo info = new YDomainBuildInfo(
					object.getName(),
					vec2WorldRectCenter.x,
					vec2WorldRectCenter.y,
					object.getWidth() / fPixelsPerUnit,
					object.getHeight() / fPixelsPerUnit);
			return builder.build(info, extraParams);
		} else
			throw new YException(builderClassName
					+ "没有实现YIDomainBuilder接口", TAG, "");

	}

	protected Object[] getExtraParams()
	{
		return null;
	}

	private Vec2 tiledCoordToBox2dCoord(Vec2 vec2Tiled, Vec2 offset,
			float fPixelsPerUnit)
	{
		Vec2 vecOppo = vec2Tiled.add(offset);

		vecOppo.y = -vecOppo.y;

		vecOppo.x = vecOppo.x / fPixelsPerUnit;
		vecOppo.y = vecOppo.y / fPixelsPerUnit;
		return vecOppo;
	}

	public static interface YIDomainBuilder
	{
		YABaseDomain build(YDomainBuildInfo info, Object[] extraParams);
	}

}
