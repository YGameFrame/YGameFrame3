package ygame.extension.tiled.domain;

import ygame.domain.YDomain;
import ygame.domain.YDomainView;

public class YDestructibleTerrainDomain extends YDomain{
	
	
	private YDestructibleTerrainLogic domainLogic;

	public YDestructibleTerrainDomain(String KEY, YDestructibleTerrainLogic logic,
			YDomainView view) {
		super(KEY, logic, view);
		this.domainLogic = logic;
		
	}

	/**
	 * 可以对地图实体进行破坏（仅限圆形爆炸区域）
	 * @param x 爆炸中心x
	 * @param y 爆炸中心y
	 * @param radius 爆炸半径
	 */
	public void destroyCircle(float x, float y, float radius) {
		domainLogic.destroyCircle(x, y, radius);
	}
}
