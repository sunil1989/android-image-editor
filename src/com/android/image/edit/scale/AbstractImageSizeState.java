package com.android.image.edit.scale;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractImageSizeState implements ImageSizeState {
	
	protected ZoomState currentScaleState = getDefaultZoomState();
	protected int originalImageWidth, originalImageHeight, viewWidth, viewHeight;
	private Map<ZoomState, Float> originalToDisplayableScales = new HashMap<ZoomState, Float>();
	
	public AbstractImageSizeState(int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
		this.originalImageWidth = originalImageWidth;
		this.originalImageHeight = originalImageHeight;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
	}

	protected abstract ImageScaleStrategy getScaleStrategy(ZoomState scaleState);
	
	protected abstract ZoomState getDefaultZoomState();

	private float getOriginalToDisplayableScale(ZoomState zoomState) {
		Float scale = originalToDisplayableScales.get(zoomState);
		if (scale == null) {
			scale = getScaleStrategy(zoomState).getScale(originalImageWidth, originalImageHeight, viewWidth, viewHeight);
			originalToDisplayableScales.put(zoomState, scale);
		}
		return scale;
	}
	
	private float getOriginalToCurrentZoomScale() {
		return getOriginalToDisplayableScale(getCurrentZoomState());
	}
	
	public float getCurrentZoomToOriginalScale() {
		return 1/getOriginalToCurrentZoomScale();
	}
	
	public float getOriginalToMaxZoomScale() {
		return getOriginalToDisplayableScale(getMaxZoomState());
	}
	
	public float getMaxZoomToCurrentZoomScale() {
		return getOriginalToCurrentZoomScale()/getOriginalToMaxZoomScale();
	}
	
	private ZoomState getMaxZoomState() {
		ZoomState[] zoomStates = ZoomState.values();
		return zoomStates[zoomStates.length-1];
	}

	@Override
	public final ZoomState getCurrentZoomState() {
		return currentScaleState;
	}
	
	@Override
	public final void performZoomAction() {
		currentScaleState = currentScaleState == ZoomState.ZOOMED_OUT ? ZoomState.ZOOMED_IN : ZoomState.ZOOMED_OUT;
	}
	
	@Override
	public int getImageWidth() {
		return round(originalImageWidth * getOriginalToCurrentZoomScale());
	}

	@Override
	public int getImageHeight() {
		return round(originalImageHeight * getOriginalToCurrentZoomScale());
	}
	
	private static int round(float a) {
		return (int)Math.ceil(a-0.5f);
	}
	
}
