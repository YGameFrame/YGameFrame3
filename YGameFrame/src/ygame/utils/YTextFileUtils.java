package ygame.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;

public final class YTextFileUtils
{
	private YTextFileUtils()
	{
	}

	/**
	 * 获取assets文件夹中文本文件的文本内容
	 * 
	 * @param strFileName
	 *                文本文件内容
	 * @param resources
	 *                资源引用
	 * @return 文本内容
	 */
	public static String getStringFromAssets(String strFileName,
			Resources resources)
	{
		String result = null;
		try
		{
			InputStream in = resources.getAssets()
					.open(strFileName);
			int ch = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((ch = in.read()) != -1)
			{
				baos.write(ch);
			}
			byte[] buff = baos.toByteArray();
			baos.close();
			in.close();
//			result = new String(buff, "UTF-8");
//			result = result.replaceAll("\\r\\n", "\n");
			result = new String(buff);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public static String getStringFromResRaw(int iResId, Resources resources)
	{
		InputStream inputStream = resources.openRawResource(iResId);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		int iLen = -1;
		byte[] buffer = new byte[512];
		try
		{
			while (-1 != (iLen = inputStream.read(buffer)))
				outputStream.write(buffer, 0, iLen);
			inputStream.close();
			return new String(outputStream.toByteArray());
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

	}

}
