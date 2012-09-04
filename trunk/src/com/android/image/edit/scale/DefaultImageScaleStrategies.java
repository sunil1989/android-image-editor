package com.android.image.edit.scale;

public enum DefaultImageScaleStrategies implements ImageScaleStrategy {
	
	ORIGINAL_SIZE {
		
		@Override
		public float getScale(int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
			return 1;
		}
		
	},
	
	FIT_TO_SCREEN_SIZE {

		@Override
		public float getScale(int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
			return Math.min(viewWidth*1.f/originalImageWidth, viewHeight*1.f/originalImageHeight);
		}
		
	},
	
	ZOOM_150_PERCENT {
		
		@Override
		public float getScale(int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
			return 1.5f;
		}
		
	};
	
}
