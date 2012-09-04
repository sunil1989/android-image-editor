package com.android.image.edit.scroll;

import android.graphics.Matrix;
import android.graphics.Rect;

public abstract class AbstractImageScrollState implements ImageScrollState {
	
	private static final float[] IDENTITY_MATRIX = {1, 0, 0, 0, 1, 0, 0, 0, 1};
	
	protected Matrix screenToCurrentZoom = new Matrix();
	protected Rect visibleRegionBounds = new Rect();
	protected int imageWidth, imageHeight, viewWidth, viewHeight;
	
	public AbstractImageScrollState(int imageWidth, int imageHeight, int viewWidth, int viewHeight) {
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
	}

	protected boolean isIdentity(Matrix matrix) {
		float[] values = new float[9];
		matrix.getValues(values);
		for (int i = 0; i < 9; i++) {
			if (Math.abs(values[i] - IDENTITY_MATRIX[i]) > 1e-3) {
				return false;
			}
		}
		return true;
	}

	public Matrix getScreenToCurrentZoom() {
		return screenToCurrentZoom;
	}
	
	@Override
	public Rect getVisibleRegionBounds() {
		return visibleRegionBounds;
	}
	
}
