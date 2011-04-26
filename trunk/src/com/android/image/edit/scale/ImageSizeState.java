package com.android.image.edit.scale;

import android.graphics.Matrix;

public interface ImageSizeState {
	
	enum ZoomAction {ZOOM_IN, ZOOM_OUT}
	
	boolean prepareDefaultScaleAndCheckFit(Matrix transform);
	
	ZoomAction getAvailableZoomAction();
	
	boolean performZoomActionAndCheckFit(Matrix transform);

}
