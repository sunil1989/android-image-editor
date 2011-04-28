package com.android.image.edit.scale;

public class LittleLessThanScreenSize extends AbstractImageSizeState {
	
	public LittleLessThanScreenSize(int originalImageWidth,
			int originalImageHeight, int viewWidth, int viewHeight) {
		super(originalImageWidth, originalImageHeight, viewWidth, viewHeight);
	}

	protected DefaultImageScaleStrategies getDefaultScaleStrategy() {
		return DefaultImageScaleStrategies.FIT_TO_SCREEN_SIZE;
	}

	@Override
	protected DefaultImageScaleStrategies getZoomedScaleStrategy() {
		return DefaultImageScaleStrategies.ZOOM_150_PERCENT;
	}

}
