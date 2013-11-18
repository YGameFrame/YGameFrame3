package ygame.framework.domain;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

class YBundleHolder
{
	@SuppressLint("UseSparseArrays")
	final Map<Integer, Object> map = new HashMap<Integer, Object>();

	float[] f_arrMVP_Matrix;
	float[] f_arrMV_Matrix;
	float[] f_arrModelMatrix;
	float[] f_arrNormalMatrix;

}
