package com.android.image.edit.scroll;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class ImageNoScrollState extends AbstractImageScrollState {
	
	int dx, dy;
	
	public ImageNoScrollState(int imageWidth, int imageHeight, int viewWidth, int viewHeight) {
		super(imageWidth, imageHeight, viewWidth, viewHeight);
		dx = (viewWidth - imageWidth)/2;
		dy = (viewHeight - imageHeight)/2;
		screenToCurrentZoom.setTranslate(-dx, -dy);
		visibleRegionBounds.set(dx, dy, imageWidth + dx, imageHeight + dy);
	}

	@Override
	public void drawBitmap(Canvas canvas, Bitmap bitmap, Matrix matrix) {
		if (matrix == null || isIdentity(matrix)) {
			canvas.drawBitmap(bitmap, dx, dy, null);
		} else {
			Matrix m = new Matrix(matrix);
			m.postTranslate(dx, dy);
			canvas.drawBitmap(bitmap, m, null);
		}
	}

	@Override
	public void setScrollByXY(float scrollByX, float scrollByY) {}
	
}
