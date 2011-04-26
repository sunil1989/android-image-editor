package com.android.image.edit.scale;

import android.graphics.Matrix;

public enum DefaultImageScaleStrategies implements ImageScaleStrategy {
	
	ORIGINAL_SIZE {
		
		@Override
		public boolean prepareTransformAndCheckFit(Matrix transform, int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
			transform.reset();
			return rotateIfNeededAndCheckFit(transform, originalImageWidth, originalImageHeight, viewWidth, viewHeight);
		}
		
	},
	
	FIT_TO_SCREEN_SIZE {

		@Override
		public boolean prepareTransformAndCheckFit(Matrix transform, int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
			transform.reset();
			if (originalImageWidth > originalImageHeight) {
				transform.postRotate(90);
				transform.postTranslate(originalImageHeight, 0);
			}
			float transformedBitmapWidth = Math.min(originalImageWidth, originalImageHeight);
			float transformedBitmapHeight = Math.max(originalImageWidth, originalImageHeight);
			float scale = Math.min(viewWidth/transformedBitmapWidth, viewHeight/transformedBitmapHeight);
			transform.postScale(scale, scale);
			return true;
		}
		
	},
	
	ZOOM_150_PERCENT {
		
		@Override
		public boolean prepareTransformAndCheckFit(Matrix transform, int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
			transform.setScale(1.5f, 1.5f);
			return rotateIfNeededAndCheckFit(transform, originalImageWidth * 1.5f, originalImageHeight * 1.5f, viewWidth, viewHeight);
		}
		
	};
	
	private void postRotate(Matrix transform, float originalImageHeight) {
		transform.postRotate(90);
		transform.postTranslate(originalImageHeight, 0);
	}
	
	protected boolean rotateIfNeededAndCheckFit(Matrix transform, float originalImageWidth, float originalImageHeight, int viewWidth, int viewHeight) {
		if (originalImageWidth <= viewWidth) {
			return originalImageHeight <= viewHeight;
		}
		if (originalImageWidth <= viewHeight) {
			if (originalImageHeight <= viewWidth) {
				postRotate(transform, originalImageHeight);
				return true;
			}
			if (originalImageHeight < originalImageWidth) {
				postRotate(transform, originalImageHeight);
			}
			return false;
		}
		if (originalImageHeight < viewHeight) {
			postRotate(transform, originalImageHeight);
		}
		return false;
	}

}
