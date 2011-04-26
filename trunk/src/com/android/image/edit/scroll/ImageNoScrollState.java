package com.android.image.edit.scroll;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class ImageNoScrollState implements ImageScrollState {
	
	private Matrix translate = new Matrix();
	private RectF visibleRegionBounds = new RectF();
	
	@Override
	public void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint, int viewWidth, int viewHeight) {
		canvas.drawBitmap(bitmap, 0, 0, paint);
		visibleRegionBounds.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
	}

	@Override
	public void setScrollByX(float scrollByX) {}

	@Override
	public void setScrollByY(float scrollByY) {}

	@Override
	public RectF getVisibleRegionBounds() {
		return visibleRegionBounds;
	}

	@Override
	public Matrix getTranslate() {
		return translate;
	}
	
}
