package ygame.extension.domain.sprite;

import ygame.framework.core.YScene;
import ygame.framework.core.YSystem;

/**
 * <b>维持状态的回调器</b>
 * 
 * <p>
 * <b>概述</b>： 无论状态时一个类还是一个枚举都应该实现该接口，以描述处于当前状态下的行为。
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
public interface YIStateClocker
{
	void onClock(float fElapseTime_s,
			YASpriteDomainLogic<?> domainLogicContext,
			YSystem system, YScene sceneCurrent);
}
