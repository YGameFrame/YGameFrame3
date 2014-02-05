package ygame.program;

import java.util.HashMap;
import java.util.Map;

import ygame.domain.YABaseShaderProgram;
import ygame.exception.YException;
import ygame.skeleton.YAttributeDataSource;
import ygame.texture.YTexture;
import android.opengl.GLES20;

/**
 * <b>抽象着色程序</b>
 * 
 * <p>
 * <b>概述</b>：使用该类时，您不需要复写{@link #onInitialize(int)}
 * 方法。此外在对着色程序赋值时（
 * {@link #applyParams(int, ygame.framework.domain.YReadBundle)}
 * ），可以利用更为方便的setXXX（如：
 * {@link #setUniformf(String, float)}等）方法实现。
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>：如果您在意效率的话，请不要采用setXXX方法赋值，改用bindXXX（如：
 * {@link #bindUniformf(int, float)}等）方法。 具体地，复写
 * {@link #onInitialize(int)}方法，利用
 * {@link #getAttribute(String)}和
 * {@link #getUniform(String)}获取相关引用存为属性，而后在
 * {@link #applyParams(int, ygame.framework.domain.YReadBundle)}
 * 中使用bindXXX方法。 </br><b>值得注意的是，您复写
 * {@link #onInitialize(int)}方法时，应该保证最先调用父类的该方法！</b>
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 * @param <DA>
 */
// 本类在父类的基础上增加了自动寻取一致变量和属性变量的能力，
// 使得客户可以少复写一个方法——onInitialize(int)，此外在对着色程序
// 每周期赋值时可以利用更为简单的"setXXX"接口完成。
// 但测试1000个粒子时，该类掉帧幅度达到10帧，原因在于
// 哈希表求哈希值、查表耗时，所以又提供了"bindXXX"接口作为一个解决方法，
// 使用bindXXX方法时，需要复写onInitialize(int)，找到变量引用用以完成bindXXX
// 参数传递（但一定要注意必须先调用super.onInitalize(int)）。
public abstract class YAShaderProgram extends YABaseShaderProgram
{

	private Map<String, Integer> uniforms;
	private Map<String, YAttributeValue> attributes;

	@Override
	protected void onInitialize(int iProgramHandle)
	{
		uniforms = getUniforms(iProgramHandle);
		attributes = getAttributes(iProgramHandle);
	}

	/**
	 * 对一致变量赋值
	 * 
	 * @param strUniformValueName
	 *                一致变量名
	 * @param x
	 *                值
	 */
	protected final void setUniformi(String strUniformValueName, int x)
	{
		bindUniformi(uniforms.get(strUniformValueName).intValue(), x);
	}

	/**
	 * 为一致变量绑定值
	 * 
	 * @param iUniformHandle
	 *                一致变量句柄
	 * @param x
	 *                值
	 */
	protected final void bindUniformi(int iUniformHandle, int x)
	{
		GLES20.glUniform1i(iUniformHandle, x);
	}

	/**
	 * 为一致变量设置纹理
	 * 
	 * @param strUniformValueName
	 *                一致变量名
	 * @param texture
	 *                纹理
	 */
	protected final void setUniformTexture(String strUniformValueName,
			YTexture texture)
	{
		bindUniformTexture(uniforms.get(strUniformValueName), texture);
	}

	/**
	 * 为一致变量绑定纹理
	 * 
	 * @param iUniformHandle
	 *                一致变量句柄
	 * @param texture
	 *                纹理
	 */
	protected final void bindUniformTexture(int iUniformHandle,
			YTexture texture)
	{
		// Set the active texture unit to
		// texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getHandle());
		// Tell the texture uniform sampler
		// to use this texture in the
		// shader by binding to texture unit
		// 0.
		setUniformf("sTexture", 0);
		bindUniformf(iUniformHandle, 0);
	}

	/**
	 * 对一致变量赋值
	 * 
	 * @param strUniformValueName
	 *                一致变量名
	 * @param x
	 *                值
	 */
	protected final void setUniformf(String strUniformValueName, float x)
	{
		bindUniformf(uniforms.get(strUniformValueName).intValue(), x);
	}

	/**
	 * 为一致变量绑定值
	 * 
	 * @param iUniformHandle
	 *                一致变量句柄
	 * @param x
	 *                值
	 */
	protected final void bindUniformf(int iUniformHandle, float x)
	{
		GLES20.glUniform1f(iUniformHandle, x);
	}

	/**
	 * 对一致变量赋值
	 * 
	 * @param strUniformValueName
	 *                一致变量名
	 * @param x
	 *                值
	 * @param y
	 *                值
	 */
	protected final void setUniformf(String strUniformValueName, float x,
			float y)
	{
		bindUniformf(uniforms.get(strUniformValueName).intValue(), x, y);
	}

	/**
	 * 为一致变量绑定值
	 * 
	 * @param iUniformHandle
	 *                一致变量句柄
	 * @param x
	 *                值
	 * @param y
	 *                值
	 */
	protected final void bindUniformf(int iUniformHandle, float x, float y)
	{
		GLES20.glUniform2f(iUniformHandle, x, y);
	}

	/**
	 * 为一致变量赋值
	 * 
	 * @param strUniformValueName
	 *                一致变量名
	 * @param x
	 *                值
	 * @param y
	 *                值
	 * @param z
	 *                值
	 */
	protected final void setUniformf(String strUniformValueName, float x,
			float y, float z)
	{
		bindUniformf(uniforms.get(strUniformValueName).intValue(), x,
				y, z);
	}

	/**
	 * 为一致变量绑定值
	 * 
	 * @param iUniformHandle
	 *                一致变量句柄
	 * @param x
	 *                值
	 * @param y
	 *                值
	 * @param z
	 *                值
	 */
	protected final void bindUniformf(int iUniformHandle, float x, float y,
			float z)
	{
		GLES20.glUniform3f(iUniformHandle, x, y, z);
	}

	/**
	 * 为一致变量设置值
	 * 
	 * @param strUniformValueName
	 *                一致变量名
	 * @param f_arrMatrixValue
	 *                值
	 */
	protected final void setUniformMatrix(String strUniformValueName,
			float[] f_arrMatrixValue)
	{
		bindUniformMatrix(uniforms.get(strUniformValueName),
				f_arrMatrixValue);
	}

	/**
	 * 为一致变量绑定值
	 * 
	 * @param iUniformHandle
	 *                一致变量句柄
	 * @param f_arrMatrixValue
	 *                值
	 */
	protected final void bindUniformMatrix(int iUniformHandle,
			float[] f_arrMatrixValue)
	{
		GLES20.glUniformMatrix4fv(iUniformHandle, 1, false,
				f_arrMatrixValue, 0);
	}

	/**
	 * 为属性变量设置数据源
	 * 
	 * @param strAttributeValueName
	 *                属性变量名
	 * @param dataSource
	 *                数据源
	 */
	protected final void setAttribute(String strAttributeValueName,
			YAttributeDataSource dataSource)
	{
		bindAttribute(attributes.get(strAttributeValueName), dataSource);
	}

	/**
	 * 为属性变量绑定数据源
	 * 
	 * @param attributeValue
	 *                属性变量
	 * @param dataSource
	 *                数据源
	 */
	protected final void bindAttribute(YAttributeValue attributeValue,
			YAttributeDataSource dataSource)
	{
		if (attributeValue.type != dataSource.type)
			throw new YException("脚本变量与数据源类型不匹配",
					YAttributeDataSource.class.getName(),
					"脚本变量类型为：" + attributeValue.type.name
							+ "，但数据源类型为："
							+ dataSource.type.name);
		bindDataSrcToValue(dataSource, attributeValue);
	}

	/**
	 * 获取属性变量
	 * 
	 * @param strAttributeValueName
	 *                属性变量名
	 * @return 属性变量
	 */
	protected final YAttributeValue getAttribute(
			String strAttributeValueName)
	{
		return attributes.get(strAttributeValueName);
	}

	/**
	 * 获取一致变量（句柄）
	 * 
	 * @param strUniformValueName
	 *                一致变量名
	 * @return 一致变量句柄
	 */
	protected int getUniform(String strUniformValueName)
	{
		return uniforms.get(strUniformValueName);
	}

	private static void bindDataSrcToValue(YAttributeDataSource dataSource,
			YAttributeValue attribute)
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,
				dataSource.getHandle());

		GLES20.glEnableVertexAttribArray(attribute.iValueHandle);
		GLES20.glVertexAttribPointer(attribute.iValueHandle,
				attribute.type.componentNum,
				attribute.type.componentType, false, 0, 0);
	}

	private static Map<String, YAttributeValue> getAttributes(
			int iProgramHandle)
	{
		Map<String, YAttributeValue> attributes = new HashMap<String, YAttributeValue>();
		// 获取属性变量个数
		int[] params = new int[1];
		GLES20.glGetProgramiv(iProgramHandle,
				GLES20.GL_ACTIVE_ATTRIBUTES, params, 0);
		int attributesNum = params[0];

		// 获取开放图形库所支持最长的名称字符数
		GLES20.glGetProgramiv(iProgramHandle,
				GLES20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, params,
				0);
		int bufsize = params[0];
		byte[] name = new byte[bufsize];
		int[] length = new int[1];
		int[] unused = new int[1];
		int[] itype = new int[1];

		for (int i = 0; i < attributesNum; i++)
		{
			// 1.获取指定索引处的属性变量的名字
			GLES20.glGetActiveAttrib(iProgramHandle, i, bufsize,
					length, 0, unused, 0, itype, 0, name, 0);
			String strName = new String(name, 0, length[0]);
			// 2.获取变量句柄
			int iValueHandle = GLES20.glGetAttribLocation(
					iProgramHandle, strName);
			// 3.获取变量类型
			YAttributeType type = YAttributeType
					.intToType(itype[0]);

			YAttributeValue attribute = new YAttributeValue(
					iValueHandle, strName, type);
			attributes.put(strName, attribute);
		}

		return attributes;
	}

	private final static Map<String, Integer> getUniforms(int iProgramHandle)
	{
		Map<String, Integer> uniforms = new HashMap<String, Integer>();

		// 获取一致变量个数
		int[] params = new int[1];
		GLES20.glGetProgramiv(iProgramHandle,
				GLES20.GL_ACTIVE_UNIFORMS, params, 0);
		int uniformsNum = params[0];

		// 获取开放图形库所支持最长的名称字符数
		GLES20.glGetProgramiv(iProgramHandle,
				GLES20.GL_ACTIVE_UNIFORM_MAX_LENGTH, params, 0);
		int bufsize = params[0];
		byte[] name = new byte[bufsize];
		int[] length = new int[1];
		int[] unused = new int[1];

		for (int i = 0; i < uniformsNum; i++)
		{
			// 1.获取指定索引处的一致变量的名字
			GLES20.glGetActiveUniform(iProgramHandle, i, bufsize,
					length, 0, unused, 0, unused, 0, name,
					0);
			String key = new String(name, 0, length[0]);

			// 2.获取句柄
			Integer value = GLES20.glGetUniformLocation(
					iProgramHandle, key);

			uniforms.put(key, value);
		}

		return uniforms;
	}

}
