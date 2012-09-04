package com.android.image.edit.scale;

public class ImageSizeStateFactory {
	
	private static ImageSizeStateFactory instance;

	private ImageSizeStateFactory() {}

	public static ImageSizeStateFactory getInstance() {
		if (instance == null) {
			instance = new ImageSizeStateFactory();
		}
		return instance;
	}
	
	public ImageSizeState createImageSizeState(int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
		float ratio = Math.max(originalImageWidth * 1.f / viewWidth, originalImageHeight * 1.f / viewHeight);
		if (ratio < 0.9) {
			return new MuchLessThanScreenSize(originalImageWidth, originalImageHeight, viewWidth, viewHeight);
		} else if (ratio > 1.5) {
			return new MuchLargerThanScreenSize(originalImageWidth, originalImageHeight, viewWidth, viewHeight);
		} else if (ratio < 1) {
			return new LittleLessThanScreenSize(originalImageWidth, originalImageHeight, viewWidth, viewHeight);
		} else {
			return new LittleLargerThanScreenSize(originalImageWidth, originalImageHeight, viewWidth, viewHeight);
		}
	}

}
