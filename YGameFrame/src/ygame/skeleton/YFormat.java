package ygame.skeleton;

import android.opengl.GLES20;

/**
 * <b>着色器中的变量类型</b>
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
public enum YFormat
{
	YVEC2("vec2", 2, GLES20.GL_FLOAT, 4), //
	YVEC3("vec3", 3, GLES20.GL_FLOAT, 4), //
	YVEC4("vec4", 4, GLES20.GL_FLOAT, 4), //

	YIVEC2("ivec2", 2, GLES20.GL_INT, 4), //
	YIVEC3("ivec3", 3, GLES20.GL_INT, 4), //
	YIVEC4("ivec4", 4, GLES20.GL_INT, 4); //

	// 暂不支持
	// YBVEC2("bvec2", 2, GLES20.GL_BOOL, 1), //
	// YBVEC3("bvec3", 3, GLES20.GL_BOOL, 1), //
	// YBVEC4("bvec4", 4, GLES20.GL_BOOL, 1);//

	/** <b>该类型在着色器中声明时的用到的关键字</b> */
	public final String KEY_WORD;
	/** <b>维度</b>：如vec2为二维 */
	public final int DIMENSION;
	/** <b>维度的类型</b>：如GLES20.GL_FLOAT等 */
	public final int DIMENSION_TYPE;
	/** <b>维度所占字节数</b> */
	public final int BYTES_PER_DIMENSION;

	private YFormat(String KEY_WORD, int DIMENSION,
			int DIMENSION_TYPE, int BYTES_PER_DIMENSION)
	{
		this.KEY_WORD = KEY_WORD;
		this.DIMENSION = DIMENSION;
		this.DIMENSION_TYPE = DIMENSION_TYPE;
		this.BYTES_PER_DIMENSION = BYTES_PER_DIMENSION;
	}
}