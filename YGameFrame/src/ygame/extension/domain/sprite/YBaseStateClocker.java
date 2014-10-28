package ygame.extension.domain.sprite;

import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.texture.YTileSheet;

public class YBaseStateClocker implements YIStateClocker
{
	private float fFrames;

	private final int iFPS;
	protected final int[] frameIndices;

	public YBaseStateClocker(int[] frameIndices, int FPS)
	{
		this.iFPS = FPS;
		this.frameIndices = frameIndices;
	}

	@Override
	public final void onClock(float fElapseTime_s,
			YASpriteDomainLogic domainLogicContext, YSystem system,
			YScene sceneCurrent)
	{
		final int iFrame = (int) ((fFrames += fElapseTime_s * iFPS) % frameIndices.length);
		YTileSheet tileSheet = domainLogicContext.getTileSheet();
		final int iRowIndex = (frameIndices[iFrame] - 1)
				/ tileSheet.COLUMN_NUM;
		final int iColumnIndex = (frameIndices[iFrame] - 1)
				% tileSheet.COLUMN_NUM;
		domainLogicContext.showImage(iColumnIndex, iRowIndex);
		onFrame(iFrame, fElapseTime_s, domainLogicContext, system,
				sceneCurrent);
	}

	protected void onFrame(int currentFrame, float fElapseTime_s,
			YASpriteDomainLogic domainLogicContext, YSystem system,
			YScene sceneCurrent)
	{
	}

}
