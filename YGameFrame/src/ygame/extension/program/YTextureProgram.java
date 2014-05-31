package ygame.extension.program;

import ygame.domain.YDomainView;
import ygame.framework.R;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YReadBundle;
import ygame.framework.domain.YWriteBundle;
import ygame.texture.YTexture;
import ygame.utils.YTextFileUtils;
import android.content.res.Resources;

/**
 * <b>纹理程序</b>
 * 
 * <p>
 * <b>概述</b>： TODO
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>： TODO
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public final class YTextureProgram extends YABlankProgram
{
	private static final int TEX = 2;

	private static YTextureProgram instance;

	public static YTextureProgram getInstance(Resources resources)
	{
		if (null == instance)
			synchronized (YTextureProgram.class)
			{
				if (null == instance)
					instance = new YTextureProgram(resources);
			}
		return instance;
//		return new YTextureProgram(resources);
	}

//	private float time;

	private YTextureProgram(Resources resources)
	{
		super(resources);
		fillCodeAndParam(YTextFileUtils.getStringFromResRaw(
				R.raw.texture_vsh, resources),
				YTextFileUtils.getStringFromResRaw(
						R.raw.texture_fsh, resources),
				YAdapter.class);
	}

	@Override
	protected void applyParams(int iProgramHandle, YReadBundle bundle,
			YSystem system, YDomainView domainView)
	{
		setUniformTexture("sTexture",
				(YTexture) bundle.readObject(TEX), 0);
//		setUniformf("time", time ++ % 360);
		super.applyParams(iProgramHandle, bundle, system, domainView);
	}

	public static class YAdapter extends
			YABlankProgram.YABlankAdapter<YAdapter>
	{
		private YTexture texture;

		public YAdapter paramTexture(YTexture texture)
		{
			this.texture = texture;
			return this;
		}

		@Override
		protected void bundleMapping(YWriteBundle bundle)
		{
			bundle.writeObject(TEX, texture);
			super.bundleMapping(bundle);
		}
	}
}
