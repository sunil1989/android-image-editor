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
	
	public ImageSizeState getImageSizeState(int originalImageWidth, int originalImageHeight, int viewWidth, int viewHeight) {
		
	}

}
