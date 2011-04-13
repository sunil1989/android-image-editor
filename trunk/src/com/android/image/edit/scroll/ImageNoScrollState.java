package com.android.image.edit.scroll;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ImageNoScrollState implements ImageScrollState {
	
	private static ImageNoScrollState instance;
	
	private ImageNoScrollState() {}
	
	public static ImageNoScrollState getInstance() {
		if (instance == null) {
			instance = new ImageNoScrollState();
		}
		return instance;
	}

	@Override
	public void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint, int viewWidth, int viewHeight) {
		canvas.drawBitmap(bitmap, 0, 0, paint);
	}

	@Override
	public void toAbsoluteCoordinates(float[] relativeCoordinates) {}

	@Override
	public void setScrollByX(float scrollByX) {}

	@Override
	public void setScrollByY(float scrollByY) {}

}
