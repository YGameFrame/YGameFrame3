package ygame.extension.domain.tilemap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import ygame.exception.YException;
import android.content.res.Resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class YTiledBean
{
	private static final String TAG = YTiledBean.class.getSimpleName();

	private int height, width;
	private int tileheight, tilewidth;
	private int version;
	// 暂未使用，目前只支持orthogonal
	// private String orientation;
	// 暂未使用，此字段意义尚不清楚
	// private String renderorder;
	private YLayer[] layers;
	private YTileSet[] tilesets;
	private YProperties properties;

	YTiledBean()
	{
	}

	public int getWidth()
	{
		return width;
	}

	void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	void setHeight(int height)
	{
		this.height = height;
	}

	public int getTileheight()
	{
		return tileheight;
	}

	void setTileheight(int tileheight)
	{
		this.tileheight = tileheight;
	}

	public int getTilewidth()
	{
		return tilewidth;
	}

	void setTilewidth(int tilewidth)
	{
		this.tilewidth = tilewidth;
	}

	public int getVersion()
	{
		return version;
	}

	void setVersion(int version)
	{
		this.version = version;
	}

	public YLayer[] getLayers()
	{
		return layers;
	}

	void setLayers(YLayer[] layers)
	{
		this.layers = layers;
	}

	public YTileSet[] getTilesets()
	{
		return tilesets;
	}

	void setTilesets(YTileSet[] tilesets)
	{
		this.tilesets = tilesets;
	}

	public YProperties getProperties()
	{
		return properties;
	}

	void setProperties(YProperties properties)
	{
		this.properties = properties;
	}

	public static class YLayer
	{
		private int height, width;
		// 暂未使用，此字段意义尚不清楚
		// private int opacity;
		// 暂未使用
		// private boolean visible;
		private int x, y;
		// 暂未使用，此字段意义尚不清楚
		// private String type;
		private String name;
		private int[] data;

		// 暂未使用，此字段意义尚不清楚，似乎只有对象层才有？
		// private String draworder;

		YLayer()
		{
		}

		public int getHeight()
		{
			return height;
		}

		void setHeight(int height)
		{
			this.height = height;
		}

		public int getWidth()
		{
			return width;
		}

		void setWidth(int width)
		{
			this.width = width;
		}

		public int getX()
		{
			return x;
		}

		void setX(int x)
		{
			this.x = x;
		}

		public int getY()
		{
			return y;
		}

		void setY(int y)
		{
			this.y = y;
		}

		public String getName()
		{
			return name;
		}

		void setName(String name)
		{
			this.name = name;
		}

		public int[] getData()
		{
			return data;
		}

		void setData(int[] data)
		{
			this.data = data;
		}

		@Override
		public String toString()
		{
			//@formatter:off
			StringBuilder sb = new StringBuilder();
			sb.append("\nname:").append(name).append('\n')
				.append("height:").append(height).append(' ').append("width:").append(width).append("\n")
				.append("x:").append(x).append(' ').append("y:").append(y).append('\n')
				.append("data:").append(Arrays.toString(data)).append('\n');
			//@formatter:on
			return sb.toString();
		}
	}

	public static class YTileSet
	{
		private int firstgid;
		private String image;
		private int imageheight, imagewidth;
		// 暂未使用
		// private int margin;
		private String name;
		private YProperties properties;
		// 暂未使用
		// private int spacing;
		private int tileheight, tilewidth;

		YTileSet()
		{
		}

		public int getFirstgid()
		{
			return firstgid;
		}

		void setFirstgid(int firstgid)
		{
			this.firstgid = firstgid;
		}

		public String getImage()
		{
			return image;
		}

		void setImage(String image)
		{
			this.image = image;
		}

		public int getImageheight()
		{
			return imageheight;
		}

		void setImageheight(int imageheight)
		{
			this.imageheight = imageheight;
		}

		public int getImagewidth()
		{
			return imagewidth;
		}

		void setImagewidth(int imagewidth)
		{
			this.imagewidth = imagewidth;
		}

		public String getName()
		{
			return name;
		}

		/**
		 * @param name
		 *                必须是放置在res/drawableXXX下的文件名！
		 */
		void setName(String name)
		{
			this.name = name;
		}

		public YProperties getProperties()
		{
			return properties;
		}

		void setProperties(YProperties properties)
		{
			this.properties = properties;
		}

		public int getTileheight()
		{
			return tileheight;
		}

		void setTileheight(int tileheight)
		{
			this.tileheight = tileheight;
		}

		public int getTilewidth()
		{
			return tilewidth;
		}

		void setTilewidth(int tilewidth)
		{
			this.tilewidth = tilewidth;
		}

		@Override
		public String toString()
		{
			//@formatter:off
			StringBuilder sb = new StringBuilder();
			sb.append("\nname:").append(name).append('\n')
				.append("firstgid:").append(firstgid).append('\n')
				.append("image:").append(image).append('\n')
				.append("imageheight:").append(imageheight).append(' ').append("imagewidth:").append(imagewidth).append("\n")
				.append("tileheight:").append(tileheight).append(' ').append("tilewidth:").append(tilewidth).append("\n")
				.append("properties:").append(properties.toString()).append('\n');
			//@formatter:on
			return sb.toString();
		}
	}

	public static class YProperties
	{
		private float unitlength;

		YProperties()
		{
		}

		public float getUnitlength()
		{
			return unitlength;
		}

		void setUnitlength(float unitlength)
		{
			this.unitlength = unitlength;
		}

		@Override
		public String toString()
		{
			return "UnitLength:" + unitlength;
		}

	}

	@Override
	public String toString()
	{
		//@formatter:off
		StringBuilder sb = new StringBuilder();
		sb.append("version:").append(version).append('\n')
			.append("height:").append(height).append(' ').append("width:").append(width).append("\n")
			.append("tileheight:").append(tileheight).append(' ').append("tilewidth:").append(tilewidth).append('\n')
			.append("properties:").append(properties.toString()).append('\n')
			.append("-----------------layers-start----------------").append('\n').append(Arrays.toString(layers)).append("-----------------layers-end----------------\n")
			.append("-----------------tilesets-start----------------").append('\n').append(Arrays.toString(tilesets)).append("-----------------tilesets-end----------------\n");
		//@formatter:on
		return sb.toString();
	}

	/**
	 * 解析利用Tiled工具生成的json文件
	 * 
	 * @param fileNameInAsset
	 *                放在asset文件夹里的json文件名
	 * @return
	 */
	public static YTiledBean parseFromTiledJson(String fileNameInAsset,
			Resources resources)
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		InputStream input = null;
		try
		{
			input = resources.getAssets().open(fileNameInAsset);
			return mapper.readValue(input, YTiledBean.class);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new YException("解析tiled_json文件失败", TAG, "");
		} finally
		{
			if (null != input)
				try
				{
					input.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
		}
	}
}
