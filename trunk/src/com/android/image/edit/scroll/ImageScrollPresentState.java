package com.android.image.edit.scroll;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

public class ImageScrollPresentState extends AbstractImageScrollState {
	
	private Rect scrollRect = new Rect(); //rect we scroll over our bitmap with
	private int scrollRectX = 0; //current left location of scroll rect
	private int scrollRectY = 0; //current top location of scroll rect
	private int visibleRegionWidth, visibleRegionHeight;
	private int dx, dy;

	public ImageScrollPresentState(int imageWidth, int imageHeight, int viewWidth, int viewHeight) {
		super(imageWidth, imageHeight, viewWidth, viewHeight);
		visibleRegionWidth = Math.min(viewWidth, imageWidth);
		visibleRegionHeight = Math.min(viewHeight, imageHeight);
		dx = (viewWidth - visibleRegionWidth)/2;
		dy = (viewHeight - visibleRegionHeight)/2;
		visibleRegionBounds.set(dx, dy, dx + visibleRegionWidth, dy + visibleRegionHeight);
		updateScrollRectAndScreenToCurrentZoom();
	}

	private void updateScrollRectAndScreenToCurrentZoom() {
		scrollRect.set(scrollRectX, scrollRectY, 
				scrollRectX + visibleRegionWidth - 1, scrollRectY + visibleRegionHeight - 1);
		screenToCurrentZoom.setTranslate(scrollRectX - dx, scrollRectY - dy);
	}

	@Override
	public void drawBitmap(Canvas canvas, Bitmap bitmap, Matrix matrix) throws IllegalArgumentException {
		if (matrix == null || isIdentity(matrix)) {
			canvas.drawBitmap(bitmap, scrollRect, visibleRegionBounds, null);
		} else {
			throw new IllegalArgumentException(new StringBuilder("This image scroll state supports only identity matrix but matrix is\n")
				.append(matrix).append(".")
				.toString());
	}
	}

	@Override
	public void setScrollByXY(float scrollByX, float scrollByY) {
		// Our move updates are calculated in ACTION_MOVE in the opposite direction
		// from how we want to move the scroll rect. Think of this as dragging to
		// the left being the same as sliding the scroll rect to the right.
		scrollRectX -= (int)scrollByX;
		scrollRectY -= (int)scrollByY;
		// Don't scroll off the left or right edges of the bitmap.
		scrollRectX = Math.min(Math.max(0, scrollRectX), imageWidth - visibleRegionWidth);

		// Don't scroll off the top or bottom edges of the bitmap.
		scrollRectY = Math.min(Math.max(0, scrollRectY), imageHeight - visibleRegionHeight);

		// We have our updated scroll rect coordinates, set them and draw.
		updateScrollRectAndScreenToCurrentZoom();
	}
	
}
