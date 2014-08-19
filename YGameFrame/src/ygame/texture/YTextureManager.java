package ygame.texture;

import java.util.HashMap;
import java.util.Map;

import ygame.exception.YException;
import android.text.TextUtils;

//暂时只管理拥有resource id的纹理对象
enum YTextureManager
{
	INSTANCE;
	
	private Map<String, YTexture> map = new HashMap<String, YTexture>();
	
	void add(YTexture texture)
	{
		if(TextUtils.isEmpty(texture.id))
			throw new YException("纹理id无效", getClass().getSimpleName(), "");
		map.put(texture.id, texture);
	}
	
	YTexture get(String id)
	{
		return map.get(id);
	}
}
