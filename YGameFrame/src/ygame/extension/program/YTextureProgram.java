package ygame.extension.program;

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
public class YTextureProgram extends YAInternalProgram
{
	private static final int TEX = 2;

	public YTextureProgram(Resources resources, int iDrawMode)
	{
		super(resources, iDrawMode);
		fillCodeAndParam(YTextFileUtils.getStringFromResRaw(
				R.raw.texture_vsh, resources),
				YTextFileUtils.getStringFromResRaw(
						R.raw.texture_fsh, resources),
				YTextureAdapter.class);
	}

	@Override
	protected void applyParams(int iProgramHandle, YReadBundle bundle,
			YSystem system)
	{
		setUniformTexture("sTexture", (YTexture) bundle.readObject(TEX));
		super.applyParams(iProgramHandle, bundle, system);
	}

	public static class YTextureAdapter extends
			YAInternalProgram.YAInternalAdapter<YTextureAdapter>
	{
		private YTexture texture;

		public YTextureAdapter paramTexture(YTexture texture)
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
