package com.android.image.edit.scale;

public class MuchLargerThanScreenSize extends AbstractImageSizeState {
	
	public MuchLargerThanScreenSize(int originalImageWidth,
			int originalImageHeight, int viewWidth, int viewHeight) {
		super(originalImageWidth, originalImageHeight, viewWidth, viewHeight);
	}

	@Override
	protected DefaultImageScaleStrategies getDefaultScaleStrategy() {
		return DefaultImageScaleStrategies.FIT_TO_SCREEN_SIZE;
	}

	@Override
	protected DefaultImageScaleStrategies getZoomedScaleStrategy() {
		return DefaultImageScaleStrategies.ORIGINAL_SIZE;
	}
	
}
