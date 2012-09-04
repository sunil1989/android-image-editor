package com.android.image.edit.scroll;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

public interface ImageScrollState {
	
	void setScrollByXY(float scrollByX, float scrollByY);
	
	void drawBitmap(Canvas canvas, Bitmap bitmap, Matrix matrix) throws IllegalArgumentException;
	
	Rect getVisibleRegionBounds();
	
	Matrix getScreenToCurrentZoom();

}
