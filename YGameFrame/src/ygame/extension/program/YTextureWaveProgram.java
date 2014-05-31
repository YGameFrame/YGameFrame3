//实验废品
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
 * @deprecated 这个只是一个实验的类，建议<b>不要使用！！！</b>
 */
@Deprecated
public final class YTextureWaveProgram extends YABlankProgram
{
	private static final int TEX = 2;

	private static YTextureWaveProgram instance;

	public static YTextureWaveProgram getInstance(Resources resources)
	{
		if (null == instance)
			synchronized (YTextureWaveProgram.class)
			{
				if (null == instance)
					instance = new YTextureWaveProgram(
							resources);
			}
		return instance;
		// return new YTextureProgram(resources);
	}

	private YTextureWaveProgram(Resources resources)
	{
		super(resources);
		fillCodeAndParam(YTextFileUtils.getStringFromResRaw(
				R.raw.texture_vsh, resources),
				YTextFileUtils.getStringFromResRaw(
						R.raw.texture_wave_fsh, resources),
						YTextureWaveAdapter.class);
	}

//	private float time;

	@Override
	protected void applyParams(int iProgramHandle, YReadBundle bundle,
			YSystem system, YDomainView domainView)
	{
		setUniformTexture("sTexture",
				(YTexture) bundle.readObject(TEX), 0);
//		setUniformf("time", time++ % 360);
//		setUniformf("time", time+=0.02f );
		super.applyParams(iProgramHandle, bundle, system, domainView);
	}

	public static class YTextureWaveAdapter extends
			YABlankProgram.YABlankAdapter<YTextureWaveAdapter>
	{
		private YTexture texture;

		public YTextureWaveAdapter paramTexture(YTexture texture)
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
