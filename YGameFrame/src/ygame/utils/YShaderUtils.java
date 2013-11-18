package ygame.utils;

import android.opengl.GLES20;

public final class YShaderUtils
{
	private YShaderUtils()
	{
	}

	private static int compileShaderSrc(String strSrc, int iShaderType)
	{
		// 创建一个新shader
		int iShader = GLES20.glCreateShader(iShaderType);
		// 加载shader的源代码
		GLES20.glShaderSource(iShader, strSrc);
		// 编译shader
		GLES20.glCompileShader(iShader);
		return iShader;
	}

	/**
	 * 创建着色程序</br> <b>您需要在GLThread线程中调用此方法！</b>
	 * 
	 * @param strVertexShaderSrc
	 *                顶点着色器代码
	 * @param strFragmentShaderSrc
	 *                片元着色器代码
	 * @return 渲染程序句柄
	 */
	public static int createProgram(String strVertexShaderSrc,
			String strFragmentShaderSrc)
	{
		// 编译顶点着色器
		int iVertexShader = compileShaderSrc(strVertexShaderSrc,
				GLES20.GL_VERTEX_SHADER);
		// 加载片元着色器
		int iFragmentShader = compileShaderSrc(strFragmentShaderSrc,
				GLES20.GL_FRAGMENT_SHADER);

		// 创建程序
		int iProgram = GLES20.glCreateProgram();
		// 向程序中加入顶点着色器与片元着色器
		GLES20.glAttachShader(iProgram, iVertexShader);
		GLES20.glAttachShader(iProgram, iFragmentShader);
		// 链接程序
		GLES20.glLinkProgram(iProgram);

		int[] linkStatus = new int[1];
		// 获取program的链接情况
		GLES20.glGetProgramiv(iProgram, GLES20.GL_LINK_STATUS,
				linkStatus, 0);
		// 若链接失败则报错并删除程序
		if (linkStatus[0] != GLES20.GL_TRUE)
			throw new RuntimeException("编译错误");

		return iProgram;
	}

}
