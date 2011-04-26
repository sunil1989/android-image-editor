package com.android.image.edit.scale;

import android.graphics.Matrix;

public enum DefaultImageSizeStates implements ImageSizeState {
	
	LITTLE_LESS_THAN_SCREEN_SIZE {
		
		protected DefaultImageScaleStrategies getDefaultScaleStrategy() {
			return DefaultImageScaleStrategies.FIT_TO_SCREEN_SIZE;
		}

		@Override
		protected DefaultImageScaleStrategies getZoomedScaleStrategy() {
			return DefaultImageScaleStrategies.ZOOM_150_PERCENT;
		}
		
	},
	
	MUCH_LESS_THAN_SCREEN_SIZE {
		
		@Override
		protected DefaultImageScaleStrategies getDefaultScaleStrategy() {
			return DefaultImageScaleStrategies.ORIGINAL_SIZE;
		}

		@Override
		protected DefaultImageScaleStrategies getZoomedScaleStrategy() {
			return DefaultImageScaleStrategies.FIT_TO_SCREEN_SIZE;
		}
		
	},
	
	LITTLE_LARGER_THAN_SCREEN_SIZE {
		
		@Override
		protected DefaultImageScaleStrategies getDefaultScaleStrategy() {
			return DefaultImageScaleStrategies.FIT_TO_SCREEN_SIZE;
		}

		@Override
		protected DefaultImageScaleStrategies getZoomedScaleStrategy() {
			return DefaultImageScaleStrategies.ZOOM_150_PERCENT;
		}
		
	},
	
	MUCH_LARGER_THAN_SCREEN_SIZE {
		
		@Override
		protected DefaultImageScaleStrategies getDefaultScaleStrategy() {
			return DefaultImageScaleStrategies.FIT_TO_SCREEN_SIZE;
		}

		@Override
		protected DefaultImageScaleStrategies getZoomedScaleStrategy() {
			return DefaultImageScaleStrategies.ORIGINAL_SIZE;
		}
		
	};
	
	protected DefaultImageScaleStrategies currentScaleStrategy = getDefaultScaleStrategy();
	protected int originalImageWidth, originalImageHeight, viewWidth, viewHeight;
	
	protected abstract DefaultImageScaleStrategies getDefaultScaleStrategy();
	
	protected abstract DefaultImageScaleStrategies getZoomedScaleStrategy();

	@Override
	public final boolean prepareDefaultScaleAndCheckFit(Matrix transform) {
		return getDefaultScaleStrategy().prepareTransformAndCheckFit(transform, originalImageWidth, originalImageHeight, viewWidth, viewHeight);
	}

	@Override
	public final ZoomAction getAvailableZoomAction() {
		return currentScaleStrategy == getDefaultScaleStrategy() ? ZoomAction.ZOOM_IN : ZoomAction.ZOOM_OUT;
	}

	@Override
	public final boolean performZoomActionAndCheckFit(Matrix transform) {
		currentScaleStrategy = currentScaleStrategy == getDefaultScaleStrategy() ? getZoomedScaleStrategy() : getDefaultScaleStrategy();
		return currentScaleStrategy.prepareTransformAndCheckFit(transform, originalImageWidth, originalImageHeight, viewWidth, viewHeight);
	}

}
