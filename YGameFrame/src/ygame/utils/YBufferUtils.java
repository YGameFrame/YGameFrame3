package ygame.utils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public final class YBufferUtils
{
	private YBufferUtils()
	{
	}

	/**
	 * 将虚拟机中的数组搬运至本地堆
	 * 
	 * @param fArray
	 *                浮点型数组
	 * @return 本地缓冲引用
	 */
	public static FloatBuffer transportArrayToNativeBuffer(float[] fArray)
	{
		ByteBuffer byteBuffer = ByteBuffer
				.allocateDirect(fArray.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		return (FloatBuffer) byteBuffer.asFloatBuffer().put(fArray)
				.position(0);
	}

	/**
	 * 将虚拟机中的数组搬运至本地堆
	 * 
	 * @param iArray
	 *                整型数组
	 * @return 本地缓冲引用
	 */
	public static IntBuffer transportArrayToNativeBuffer(int[] iArray)
	{
		ByteBuffer byteBuffer = ByteBuffer
				.allocateDirect(iArray.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		return (IntBuffer) byteBuffer.asIntBuffer().put(iArray)
				.position(0);
	}

	/**
	 * 将虚拟机中的数组搬运至本地堆
	 * 
	 * @param fArray
	 *                短整型数组
	 * @return 本地缓冲引用
	 */
	public static ShortBuffer transportArrayToNativeBuffer(short[] sArray)
	{
		ByteBuffer byteBuffer = ByteBuffer
				.allocateDirect(sArray.length * 2);
		byteBuffer.order(ByteOrder.nativeOrder());
		return (ShortBuffer) byteBuffer.asShortBuffer().put(sArray)
				.position(0);
	}

	/**
	 * 将本地缓冲上传到显存
	 * 
	 * @param bufferToUpload
	 *                要上传的本地缓冲
	 * @param iTarget
	 *                上传的目标槽
	 * @param iBytesPerElement
	 *                缓冲中单个元素占用的字节数
	 *                （所谓元素指的是float、
	 *                short等java中的基本数据类型）
	 * @param iUsage
	 *                用途
	 * @return 在显存中的句柄
	 */
	public static int upload(Buffer bufferToUpload, int iTarget,
			int iBytesPerElement, int iUsage)
	{
		int buff[] = new int[1];
		GLES20.glGenBuffers(1, buff, 0);
		int handle = buff[0];

		GLES20.glBindBuffer(iTarget, handle);
		GLES20.glBufferData(iTarget, bufferToUpload.limit()
				* iBytesPerElement, bufferToUpload, iUsage);
		GLES20.glBindBuffer(iTarget, 0);
		return handle;
	}
}
