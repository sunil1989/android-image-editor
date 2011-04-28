package com.android.image.edit.scale;

public class MuchLessThanScreenSize extends AbstractImageSizeState {
	
	public MuchLessThanScreenSize(int originalImageWidth,
			int originalImageHeight, int viewWidth, int viewHeight) {
		super(originalImageWidth, originalImageHeight, viewWidth, viewHeight);
	}

	@Override
	protected DefaultImageScaleStrategies getDefaultScaleStrategy() {
		return DefaultImageScaleStrategies.ORIGINAL_SIZE;
	}

	@Override
	protected DefaultImageScaleStrategies getZoomedScaleStrategy() {
		return DefaultImageScaleStrategies.FIT_TO_SCREEN_SIZE;
	}
	
}
