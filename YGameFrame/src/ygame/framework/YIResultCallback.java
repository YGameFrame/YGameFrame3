package ygame.framework;

/**
 * <b>结果回调</b>
 * 
 * <p>
 * <b>概述</b>：一般用于返回异步结果，其结果返回在{@link #onResultReceived(Object)}
 * 函数的参数objResult中，您可以根据对应情况将之强制类型转为其它对象而获取其异步返回的信息。
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
public interface YIResultCallback
{
	/**
	 * 参见{@link YIResultCallback}
	 * 
	 * @param objResult
	 *                异步返回的结果，您可以根据具体情况将之强制类型转换，进而获取异步返回的信息
	 */
	void onResultReceived(Object objResult);
}
