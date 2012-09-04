package com.android.image.edit.scale;

public class LittleLargerThanScreenSize extends AbstractImageSizeState {
	
	public LittleLargerThanScreenSize(int originalImageWidth,
			int originalImageHeight, int viewWidth, int viewHeight) {
		super(originalImageWidth, originalImageHeight, viewWidth, viewHeight);
	}

	@Override
	protected ZoomState getDefaultZoomState() {
		return ZoomState.ZOOMED_OUT;
	}

	@Override
	protected ImageScaleStrategy getScaleStrategy(ZoomState zoomState) {
		switch (zoomState) {
		case ZOOMED_OUT:
			return DefaultImageScaleStrategies.FIT_TO_SCREEN_SIZE;
		case ZOOMED_IN:
		return DefaultImageScaleStrategies.ZOOM_150_PERCENT;
		default: return null;
		}
	}

}
