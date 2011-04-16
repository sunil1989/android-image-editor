package com.android.image.edit.scroll;

import com.android.image.edit.ImageEditorView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public interface ImageScrollState {
	
	void setScrollByX(float scrollByX);
	
	void setScrollByY(float scrollByY);
	
	void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint, int viewWidth, int viewHeight);
	
	void toAbsoluteCoordinates(float[] relativeCoordinates);
	
	void toRelativeCoordinates(float[] originalCoordinates);
	
	RectF getVisibleRegionBounds(ImageEditorView context);

}
