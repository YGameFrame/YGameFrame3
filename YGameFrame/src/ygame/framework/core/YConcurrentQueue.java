package ygame.framework.core;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <b>无锁同步队列实现</b>
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
 */
public final class YConcurrentQueue<E> implements YIConcurrentQueue<E>
{

	private ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();

	@Override
	public boolean enqueue(E element)
	{
		return queue.offer(element);
	}

	@Override
	public E dequeue()
	{
		return queue.poll();
	}

}
