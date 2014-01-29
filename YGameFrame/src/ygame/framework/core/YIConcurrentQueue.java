package ygame.framework.core;

/**
 * <b>无锁同步队列</b>
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
 * @param <E>
 *                元素类型
 */
public interface YIConcurrentQueue<E>
{
	/**
	 * 入队
	 * 
	 * @param element
	 *                入队元素
	 * @return 入队成功返回真，反之则反
	 */
	boolean enqueue(E element);

	/**
	 * 出队
	 * 
	 * @return 出队元素
	 */
	E dequeue();
}
