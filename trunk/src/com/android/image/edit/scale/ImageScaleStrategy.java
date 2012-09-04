package com.android.image.edit.scale;

public interface ImageScaleStrategy {
	
	float getScale(int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight);

}
