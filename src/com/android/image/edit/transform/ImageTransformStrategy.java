package com.android.image.edit.transform;

import android.graphics.Matrix;

public enum ImageTransformStrategy {
	
	ORIGINAL_SIZE {
		
		private void postRotate(Matrix transform, int originalImageHeight) {
			transform.postRotate(90);
			transform.postTranslate(originalImageHeight, 0);
		}
		
		@Override
		public boolean prepareTransformAndCheckFit(Matrix transform, int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
			transform.reset();
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
			/*if (imageFitsIntoView(originalImageWidth, originalImageHeight, viewWidth, viewHeight)) {
				return true;
			}
			if (imageFitsIntoView(originalImageHeight, originalImageWidth, viewWidth, viewHeight)) {
				transform.postRotate(90);
				transform.postTranslate(originalImageHeight, 0);
				return true;
			}
			
			return false;*/
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
		
	};
	
	public abstract boolean prepareTransformAndCheckFit(Matrix transform, int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight);
	
	protected boolean imageFitsIntoView(int imageWidth, int imageHeight, int viewWidth, int viewHeight) {
		return imageWidth < viewWidth && imageHeight < viewHeight;
	}

}
