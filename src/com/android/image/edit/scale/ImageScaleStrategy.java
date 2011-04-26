package com.android.image.edit.scale;

import android.graphics.Matrix;

public interface ImageScaleStrategy {
	
	boolean prepareTransformAndCheckFit(Matrix transform, int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight);

}
