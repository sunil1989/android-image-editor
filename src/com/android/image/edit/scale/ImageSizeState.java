package com.android.image.edit.scale;

import android.graphics.Matrix;

public interface ImageSizeState {
	
	enum ZoomAction {ZOOM_IN, ZOOM_OUT}
	
	boolean prepareScaleAndCheckFit(Matrix transform);
	
	ZoomAction getAvailableZoomAction();
	
	void performZoomAction();
	
	//boolean performZoomActionAndCheckFit(Matrix transform);

}
