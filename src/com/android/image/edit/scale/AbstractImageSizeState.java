package com.android.image.edit.scale;

import android.graphics.Matrix;

public abstract class AbstractImageSizeState implements ImageSizeState {
	
	protected ImageScaleStrategy currentScaleStrategy = getDefaultScaleStrategy();
	protected int originalImageWidth, originalImageHeight, viewWidth, viewHeight;
	
	public AbstractImageSizeState(int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
		this.originalImageWidth = originalImageWidth;
		this.originalImageHeight = originalImageHeight;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
	}

	protected abstract ImageScaleStrategy getDefaultScaleStrategy();
	
	protected abstract DefaultImageScaleStrategies getZoomedScaleStrategy();

	@Override
	public final boolean prepareScaleAndCheckFit(Matrix transform) {
		return currentScaleStrategy.prepareTransformAndCheckFit(transform, originalImageWidth, originalImageHeight, viewWidth, viewHeight);
	}

	@Override
	public final ZoomAction getAvailableZoomAction() {
		return currentScaleStrategy == getDefaultScaleStrategy() ? ZoomAction.ZOOM_IN : ZoomAction.ZOOM_OUT;
	}
	
	@Override
	public final void performZoomAction() {
		currentScaleStrategy = currentScaleStrategy == getDefaultScaleStrategy() ? getZoomedScaleStrategy() : getDefaultScaleStrategy();
	}
	
}
