package ygame.program;

/**
 * <b>属性变量</b>
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
public final class YAttributeValue
{
	final int iValueHandle;
	final String strName;
	final YAttributeType type;

	YAttributeValue(int iValueHandle, String strName, YAttributeType type)
	{
		this.iValueHandle = iValueHandle;
		this.strName = strName;
		this.type = type;
	}

}