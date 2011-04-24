package com.android.image.edit.scroll;

import com.android.image.edit.ImageEditorView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

public class ImageNoScrollState implements ImageScrollState {
	
	Matrix translate = new Matrix();
	
	@Override
	public void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint, int viewWidth, int viewHeight) {
		canvas.drawBitmap(bitmap, 0, 0, paint);
	}

	@Override
	public void setScrollByX(float scrollByX) {}

	@Override
	public void setScrollByY(float scrollByY) {}

	@Override
	public RectF getVisibleRegionBounds(ImageEditorView context) {
		Bitmap transformedBitmap = context.getTransformedBitmap();
		return new RectF(0, 0, transformedBitmap.getWidth(), transformedBitmap.getHeight());
	}

	@Override
	public Matrix getTranslate() {
		return translate;
	}

	@Override
	public Point getTopLeftCorner() {
		return new Point(0, 0);
	}
	
}
