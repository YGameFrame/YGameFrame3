package ygame.extension.domain.tilemap;

import ygame.domain.YADomainLogic;
import ygame.domain.YDomain;
import ygame.extension.domain.tilemap.YTileMapDomain.YMapComponentData;
import ygame.extension.program.YTileMapProgram.YAdapter;
import ygame.framework.core.YRequest;
import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YWriteBundle;
import ygame.math.YMatrix;
import ygame.skeleton.YSkeleton;
import ygame.texture.YTexture;
import ygame.texture.YTileSheet;
import ygame.transformable.YMover;

class YLargeMapComponentDomainLogic extends YADomainLogic
{

	private YMover mover = new YMover();
	private YSkeleton skeleton;
	private YTexture textureData;
	private YTileSheet tileSheetGraphics;

	public YLargeMapComponentDomainLogic(YMapComponentData data)
	{
		this.skeleton = data.skeleton;
		this.textureData = data.textureData;
		this.tileSheetGraphics = data.tileSheet;
		mover.setX(data.vectorPosition.x).setY(
				data.vectorPosition.y);
	}

	@Override
	protected void onCycle(double dbElapseTime_s, YDomain domainContext,
			YWriteBundle bundle, YSystem system,
			YScene sceneCurrent, YMatrix matrix4pv,
			YMatrix matrix4Projection, YMatrix matrix4View)
	{
		YAdapter adapter = (YAdapter) domainContext
				.getParametersAdapter();
		adapter.paramMatrixPV(matrix4pv).paramMover(mover)
				.paramSkeleton(skeleton)
				.paramTileMapData(textureData)
				.paramTileMapGraphics(tileSheetGraphics);
	}

	@Override
	protected boolean onDealRequest(YRequest request, YSystem system,
			YScene sceneCurrent)
	{
		return false;
	}


}
