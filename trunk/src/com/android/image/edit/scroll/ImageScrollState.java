package com.android.image.edit.scroll;

import com.android.image.edit.ImageEditorView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

public interface ImageScrollState {
	
	void setScrollByX(float scrollByX);
	
	void setScrollByY(float scrollByY);
	
	void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint, int viewWidth, int viewHeight);
	
	RectF getVisibleRegionBounds(ImageEditorView context);
	
	Matrix getTranslate();
	
	Point getTopLeftCorner();

}
