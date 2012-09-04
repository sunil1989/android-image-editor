package com.android.image.edit.scale;

public interface ImageSizeState {
	
	enum ZoomState {ZOOMED_OUT, ZOOMED_IN}
	
	ZoomState getCurrentZoomState();
	
	void performZoomAction();
	
	int getImageWidth();
	
	int getImageHeight();
	
	float getOriginalToMaxZoomScale();
	
	float getMaxZoomToCurrentZoomScale();
	
	float getCurrentZoomToOriginalScale();

}
